package com.chenxiaofeng.aibi.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenxiaofeng.aibi.bizmq.BiMessageProducer;
import com.chenxiaofeng.aibi.constant.BiConstant;
import com.chenxiaofeng.aibi.constant.FileConstant;
import com.chenxiaofeng.aibi.manager.AiManager;
import com.chenxiaofeng.aibi.manager.RedisLimiterManager;
import com.chenxiaofeng.aibi.model.dto.chart.*;
import com.chenxiaofeng.aibi.model.enums.ChartStatusEnum;
import com.chenxiaofeng.aibi.model.vo.chart.BiResponse;
import com.chenxiaofeng.aibi.utils.ExcelUtils;
import com.chenxiaofeng.aibi.annotation.AuthCheck;
import com.chenxiaofeng.aibi.common.BaseResponse;
import com.chenxiaofeng.aibi.common.DeleteRequest;
import com.chenxiaofeng.aibi.common.ErrorCode;
import com.chenxiaofeng.aibi.common.ResultUtils;
import com.chenxiaofeng.aibi.constant.CommonConstant;
import com.chenxiaofeng.aibi.constant.UserConstant;
import com.chenxiaofeng.aibi.exception.BusinessException;
import com.chenxiaofeng.aibi.exception.ThrowUtils;
import com.chenxiaofeng.aibi.model.entity.Chart;
import com.chenxiaofeng.aibi.model.entity.User;
import com.chenxiaofeng.aibi.service.ChartService;
import com.chenxiaofeng.aibi.service.UserService;
import com.chenxiaofeng.aibi.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;



@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

    @Resource
    private AiManager aiManager;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private BiMessageProducer biMessageProducer;


    // region 增删改查

    /**
     * 创建图表
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newChartId = chart.getId();
        return ResultUtils.success(newChartId);
    }



    /**
     * 删除图表
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }



    /**
     * 更新图表（仅管理员）
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        long id = chartUpdateRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }



    /**
     * 根据 id 获取图表
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }



    /**
     * 分页获取列表（封装类）
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
            HttpServletRequest request) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }



    /**
     * 分页获取当前用户创建的资源列表
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<Chart>> listMyChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
            HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    // endregion

    /**
     * 智能分析
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen")
    public BaseResponse<BiResponse> getChartByAi(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        //参数校验
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件为空");
        String goal = genChartByAiRequest.getGoal();
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "分析目标为空");
        String chartName = genChartByAiRequest.getChartName();
        ThrowUtils.throwIf(StringUtils.isBlank(chartName) && chartName.length() > 100, ErrorCode.PARAMS_ERROR, "图表名称为空");
        String chartType = genChartByAiRequest.getChartType();

        //校验文件大小
        long size = multipartFile.getSize();
        ThrowUtils.throwIf(size > FileConstant.MAX_FILE_SIZE, ErrorCode.SYSTEM_ERROR, "文件超过10M");
        String originalFilename = multipartFile.getOriginalFilename();
        //检验文件后缀
        String fileSuffix = FileUtil.getSuffix(originalFilename);
        ThrowUtils.throwIf(!BiConstant.VALID_FILE_SUFFIX_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件格式有误");

        //获取登录用户
        User loginUser = userService.getLoginUser(request);
        final String key = BiConstant.BI_REDIS_LIMITER_KEY + loginUser.getId();

        // 限流判断，每个用户一个限流器
        redisLimiterManager.doRateLimit(key);

        // 用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        //拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append(goal).append("\n");
        userInput.append("原始数据：").append("\n");

        //csv数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(csvData).append("\n");

        //向AI提问
        String aiRes = aiManager.retryDoChat(userInput.toString());

        //截取AI回答的数据
        final String str = "【【【【【";
        String[] aiData = aiRes.split(str);
        ThrowUtils.throwIf(aiData.length < 3, ErrorCode.SYSTEM_ERROR, "AI生成错误");
        String genChart = aiData[1].trim();
        String genResult = aiData[2].trim();

        //插入数据到数据库
        Chart chart = new Chart();
        chart.setGoal(userGoal);
        chart.setChartName(chartName);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setUserId(loginUser.getId());
        chart.setGenStatus(ChartStatusEnum.SUCCEED.getValue());
        ThrowUtils.throwIf(!chartService.save(chart), ErrorCode.SYSTEM_ERROR, "图表保存失败");

        //返回AI对话数据
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        return ResultUtils.success(biResponse);
    }



    @PostMapping("/gen/async")
    public BaseResponse<BiResponse> getChartByAiAsync(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        //参数校验
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件为空");
        String goal = genChartByAiRequest.getGoal();
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "分析目标为空");
        String chartName = genChartByAiRequest.getChartName();
        ThrowUtils.throwIf(StringUtils.isBlank(chartName) && chartName.length() > 100, ErrorCode.PARAMS_ERROR, "图表名称为空");
        String chartType = genChartByAiRequest.getChartType();
        //校验文件
        long size = multipartFile.getSize();
        ThrowUtils.throwIf(size > FileConstant.MAX_FILE_SIZE, ErrorCode.SYSTEM_ERROR, "文件超过1M");
        String originalFilename = multipartFile.getOriginalFilename();
        String fileSuffix = FileUtil.getSuffix(originalFilename);
        ThrowUtils.throwIf(!BiConstant.VALID_FILE_SUFFIX_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件格式有误");

        //获取登录用户
        User loginUser = userService.getLoginUser(request);
        final String key = BiConstant.BI_REDIS_LIMITER_KEY + loginUser.getId();
        // 限流判断，每个用户一个限流器
        redisLimiterManager.doRateLimit(key);

        // 用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        //拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append(goal).append("\n");
        userInput.append("原始数据：").append("\n");
        //csv数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(csvData).append("\n");

        //在ai对话前将图表数据入库 状态为 wait
        Chart chart = new Chart();
        chart.setGoal(userGoal);
        chart.setChartName(chartName);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setUserId(loginUser.getId());
        chart.setGenStatus(ChartStatusEnum.WAIT.getValue());
        ThrowUtils.throwIf(!chartService.save(chart), ErrorCode.SYSTEM_ERROR, "图表保存失败");

        Long chartId = chart.getId();


        //将AI对话放到线程池中去执行
        CompletableFuture.runAsync(() -> {
            //修改图表状态为 running
            chartService.handleChartStatus(chartId, ChartStatusEnum.RUNNING.getValue());

            //向AI提问
            String aiRes = aiManager.retryDoChat(userInput.toString());
            //处理AI返回数据
            final String str = "【【【【【";
            String[] aiData = aiRes.split(str);

            ThrowUtils.throwIf(aiData.length < 3, ErrorCode.SYSTEM_ERROR, "AI生成错误");
            String genChart = aiData[ 1 ].trim();
            String genResult = aiData[ 2 ].trim();

            //更新 图表数据
            Chart updateChart = new Chart();
            updateChart.setId(chartId);
            updateChart.setGenChart(genChart);
            updateChart.setGenResult(genResult);
            updateChart.setGenStatus(ChartStatusEnum.SUCCEED.getValue());
            if (!chartService.updateById(updateChart)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新图表失败");
            }
        }, threadPoolExecutor).exceptionally(e -> {
            //修改图表状态为 fail
            chartService.handleChartStatus(chart.getId(), ChartStatusEnum.FAIL.getValue());
            return null;
        });
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chartId);
        return ResultUtils.success(biResponse);
    }



    @PostMapping("/gen/async/mq")
    public BaseResponse<BiResponse> getChartByAiAsyncMq(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        //参数校验
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件为空");
        String goal = genChartByAiRequest.getGoal();
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "分析目标为空");
        String chartName = genChartByAiRequest.getChartName();
        ThrowUtils.throwIf(StringUtils.isBlank(chartName) && chartName.length() > 100, ErrorCode.PARAMS_ERROR, "图表名称为空");
        String chartType = genChartByAiRequest.getChartType();
        //校验文件
        long size = multipartFile.getSize();
        ThrowUtils.throwIf(size > FileConstant.MAX_FILE_SIZE, ErrorCode.SYSTEM_ERROR, "文件超过1M");
        String originalFilename = multipartFile.getOriginalFilename();
        String fileSuffix = FileUtil.getSuffix(originalFilename);
        ThrowUtils.throwIf(!BiConstant.VALID_FILE_SUFFIX_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件格式有误");

        //获取登录用户
        User loginUser = userService.getLoginUser(request);
        final String key = BiConstant.BI_REDIS_LIMITER_KEY + loginUser.getId();
        // 限流判断，每个用户一个限流器
        redisLimiterManager.doRateLimit(key);

        // 用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        //拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append(goal).append("\n");
        userInput.append("原始数据：").append("\n");
        //csv数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(csvData).append("\n");

        //在ai对话前将图表数据入库 状态为 wait
        Chart chart = new Chart();
        chart.setGoal(userGoal);
        chart.setChartName(chartName);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setUserId(loginUser.getId());
        chart.setGenStatus(ChartStatusEnum.WAIT.getValue());
        boolean saveRes = chartService.save(chart);
        ThrowUtils.throwIf(!saveRes, ErrorCode.SYSTEM_ERROR, "图表保存失败");

        Long newChartId = chart.getId();

        //向消息队列发送消息
        biMessageProducer.sendMessage(String.valueOf(newChartId));

        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(newChartId);
        return ResultUtils.success(biResponse);
    }



    @GetMapping("/reload/gen")
    public BaseResponse<Boolean> reloadChartByAi(long chartId, HttpServletRequest request) {
        return ResultUtils.success(chartService.reloadChartByAi(chartId, request));
    }



    /**
     * 获取查询包装类
     * @param chartQueryRequest
     * @return
     */
    private QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getChartName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

}
