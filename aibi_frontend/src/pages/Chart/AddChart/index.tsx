import { CHART_TYPE } from '@/constants/chart/chartType';
import { getChartByAiUsingPost } from '@/services/aibi/chartController';
import { UploadOutlined } from '@ant-design/icons';
import { Button, Card, Col, Divider, Form, Input, message, Row, Select, Space, Spin } from 'antd';
import TextArea from 'antd/es/input/TextArea';
import Upload from 'antd/es/upload/Upload';
import ReactECharts from 'echarts-for-react';
import React, { useState } from 'react';


const AddChart: React.FC = () => {
  // const [form] = useForm();
  const [chart, setChart] = useState<API.BiResponse>();
  const [option, setOption] = useState<any>();
  const [submitting, setSubmitting] = useState<boolean>(false);

  // const normFile = (e: any) => {
  //   console.log('Upload event:', e);
  //   if (Array.isArray(e)) {
  //     return e;
  //   }
  //   return e && e.fileList;
  // };

  const onFinish = async (values: any) => {
    if (submitting) {
      return;
    }
    setSubmitting(true);
    //清空数据
    setOption(undefined);
    setChart(undefined);
    const params = {
      ...values,
      file: undefined,
    };
    try {
      //文件原始对象
      const file = values.file.file.originFileObj;
      const res = await getChartByAiUsingPost(params, {}, file);
      const data = res?.data;
      if (res.code === 0 && data) {
        message.success('分析成功');
        const chartOption = JSON.parse(data.genChart ?? '');
        if (!chartOption) {
          throw new Error('图表代码解析失败');
        } else {
          setChart(data);
          setOption(chartOption);
        }
      } else {
        message.error('分析失败');
      }
    } catch (e: any) {
      message.error('分析失败');
    }
    setSubmitting(false);
  };

  return (
    <div className="add-chart">
      <Row gutter={24}>
        <Col span={12}>
          <Card title="添加图表">
            <Form
              labelCol={{ span: 6 }}
              wrapperCol={{ span: 16 }}
              labelAlign={'left'}
              onFinish={onFinish}
            >
              <Form.Item
                name="goal"
                label="分析目标："
                rules={[{ required: true, message: '请输入分析目标' }]}
              >
                <TextArea allowClear showCount maxLength={100} placeholder="请输入你的分析需求" />
              </Form.Item>
              <Form.Item name="chartName" label="图表名称">
                <Input allowClear placeholder="请输入你的图表名称" />
              </Form.Item>
              <Form.Item name="chartType" label="图表类型">
                <Select placeholder="请输选择图表类型" options={CHART_TYPE} />
              </Form.Item>
              <Form.Item
                name="file"
                label="原始数据"
                rules={[{ required: true, message: '请上传原始数据' }]}
                // valuePropName="fileList"
                // getValueFromEvent={normFile}
              >
                <Upload name="file" maxCount={1}>
                  <Button icon={<UploadOutlined />}>点击上传</Button>
                </Upload>
              </Form.Item>
              <Form.Item wrapperCol={{ span: 16, offset: 6 }}>
                <Space>
                  <Button
                    type="primary"
                    htmlType="submit"
                    loading={submitting}
                    disabled={submitting}
                  >
                    提交
                  </Button>
                  <Button htmlType="reset">重置</Button>
                </Space>
              </Form.Item>
            </Form>
          </Card>
        </Col>
        <Col span={12}>
          <Card title="可视化图表">
            {option ? <ReactECharts option={option} /> : <div>请先在左侧完成提交</div>}
            <Spin spinning={submitting} />
          </Card>
        </Col>
        <Divider style={{ border: 'none' }} />
        <Col span={24}>
          <Card title="分析结论">
            {chart?.genResult ?? <div>请先在左侧完成提交</div>}
            <Spin spinning={submitting} />
          </Card>
        </Col>
      </Row>
    </div>
  );
};
export default AddChart;
