import { CHART_STATUS } from '@/constants/chart/chartStatus';
import { CHART_TYPE_JSON } from '@/constants/chart/chartType';
import CreateChartModal from '@/pages/Admin/Chart/components/CreateChartModal';
import UpdateChartModal from '@/pages/Admin/Chart/components/UpdateChartModal';
import { deleteChartUsingPost, listChartByPageUsingPost } from '@/services/aibi/chartController';
import { ActionType, PageContainer, ProColumns, ProTable } from '@ant-design/pro-components';
import { Button, Divider, message, Popconfirm, Space, Typography } from 'antd';
import React, { useRef, useState } from 'react';

/**
 * 图表管理页面
 * @constructor
 */
const AdminChartPage: React.FC<unknown> = () => {
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);
  const [updateModalVisible, setUpdateModalVisible] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);
  const [updateData, setUpdateData] = useState<API.Chart>({});
  const actionRef = useRef<ActionType>();


  /**
   *  删除节点
   * @param selectedRows
   */
  const doDelete = async (selectedRows: API.Chart[]) => {
    const hide = message.loading('正在删除');
    if (!selectedRows) return true;
    try {
      await deleteChartUsingPost({
        id: selectedRows.find((row) => row.id)?.id || 0,
      });
      message.success('操作成功');
      actionRef.current?.reload();
    } catch (e: any) {
      message.error('操作失败，' + e.message);
    } finally {
      hide();
    }
  };

  /**
   * 表格列配置
   */
  const columns: ProColumns<API.Chart>[] = [
    {
      title: 'id',
      dataIndex: 'id',
      valueType: 'index',
    },
    {
      title: '图表名称',
      dataIndex: 'chartName',
      valueType: 'text',
    },
    {
      title: '图表类型',
      dataIndex: 'chartType',
      valueType: 'text',
      valueEnum: CHART_TYPE_JSON,
    },
    {
      title: '原始数据',
      dataIndex: 'chartData',
      valueType: 'text',
    },
    {
      title: '分析目标',
      dataIndex: 'goal',
      valueType: 'textarea',
    },
    {
      title: 'AI生成图表数据',
      dataIndex: 'genChart',
      valueType: 'text',
    },
    {
      title: 'AI分析结论',
      dataIndex: 'genResult',
      valueType: 'textarea',
    },
    // {
    //   title: '创建用户',
    //   dataIndex: 'userId',
    //   valueType: 'text',
    //   copyable: true,
    // },
    {
      title: '生成状态',
      dataIndex: 'genStatus',
      valueType: 'text',
      valueEnum: CHART_STATUS,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInForm: true,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      valueType: 'dateTime',
      hideInForm: true,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => (
        <Space split={<Divider type="vertical" />}>
          <Typography.Link
            onClick={() => {
              setUpdateData(record);
              setUpdateModalVisible(true);
            }}
          >
            修改
          </Typography.Link>
          <Popconfirm
            title="您确定要删除么？"
            onConfirm={() => doDelete([record])}
            okText="确认"
            cancelText="取消"
          >
            <Typography.Link type="danger">删除</Typography.Link>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer>
      <ProTable<API.Chart>
        headerTitle="图表管理"
        actionRef={actionRef}
        loading={loading}
        rowKey="id"
        search={{
          labelWidth: 'auto',
        }}
        toolBarRender={() => [
          <Button key="1" type="primary" onClick={() => setCreateModalVisible(true)}>
            新建
          </Button>,
        ]}
        request={async (params) => {
          setLoading(true);
          const searchParams: API.ChartQueryRequest = {
            ...params,
            sortField: 'createTime',
            sortOrder: 'desc',
          };
          console.log(searchParams);
          const { data, code } = await listChartByPageUsingPost(searchParams);
          setLoading(false);
          return {
            data: data?.records || [],
            success: code === 0,
            total: data?.total,
          } as any;
        }}
        columns={columns}
      />
      <CreateChartModal
        modalVisible={createModalVisible}
        columns={columns}
        onSubmit={() => {}}
        onCancel={() => setCreateModalVisible(false)}
      />
      <UpdateChartModal
        oldData={updateData}
        modalVisible={updateModalVisible}
        columns={columns}
        onSubmit={() => {}}
        onCancel={() => setUpdateModalVisible(false)}
      />
    </PageContainer>
  );
};

export default AdminChartPage;
