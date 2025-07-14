import { CHART_TYPE } from '@/constants/chart/chartType';
import { getChartByAiAsyncUsingPost } from '@/services/aibi/chartController';
import { UploadOutlined } from '@ant-design/icons';
import { ProForm } from '@ant-design/pro-form';
import { Button, Card, Col, Form, Input, message, Row, Select, Space } from 'antd';
import TextArea from 'antd/es/input/TextArea';
import Upload from 'antd/es/upload/Upload';
import React from 'react';
import useForm = ProForm.useForm;

const AddChartSync: React.FC = () => {
  const [form] = useForm();
  const onFinish = async (values: any) => {
    const params = {
      ...values,
      file: undefined,
    };
    try {
      //文件原始对象
      const file = values.file.file.originFileObj;
      const res = await getChartByAiAsyncUsingPost(params, {}, file);
      const data = res?.data;
      if (res.code === 0 && data) {
        form.resetFields(); // 重置表单
        message.success('分析任务提交成功，稍后请前往我的图表中查看');
      } else {
        message.error('分析失败');
      }
    } catch (e: any) {
      message.error('分析失败');
    }
  };

  return (
    <div className="add-chart">
      <Row gutter={24}>
        <Col span={24}>
          <Card title="添加图表">
            <Form
              form={form}
              labelCol={{ span: 4 }}
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

              >
                <Upload name="file" maxCount={1}>
                  <Button icon={<UploadOutlined />}>点击上传</Button>
                </Upload>
              </Form.Item>
              <Form.Item wrapperCol={{ span: 16, offset: 6 }}>
                <Space>
                  <Button type="primary" htmlType="submit">
                    提交
                  </Button>
                  <Button htmlType="reset">重置</Button>
                </Space>
              </Form.Item>
            </Form>
          </Card>
        </Col>
      </Row>
    </div>
  );
};
export default AddChartSync;
