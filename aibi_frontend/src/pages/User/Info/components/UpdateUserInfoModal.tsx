
import { updateUserInfoUsingPost } from '@/services/aibi/userController';
import { ProColumns, ProTable } from '@ant-design/pro-components';
import { message, Modal } from 'antd';
import React, { PropsWithChildren } from 'react';

interface UpdateModalProps {
  oldData: API.User;
  modalVisible: boolean;
  columns: ProColumns<API.User>[];
  onSubmit: () => void;
  onCancel: () => void;
}

/**
 * 更新数据模态框
 * @param fields
 */
const handleUpdate = async (fields: API.User) => {
  const hide = message.loading('正在修改');
  try {
    await updateUserInfoUsingPost({
      id: fields.id ?? 0,
      ...fields,
    });
    hide();
    message.success('修改成功');
    return true;
  } catch (error) {
    hide();
    message.error('修改失败请重试！');
    return false;
  }
};

/**
 * 更新数据模态框
 * @param props
 * @constructor
 */
const UpdateUserInfoModal: React.FC<PropsWithChildren<UpdateModalProps>> = (props) => {
  const { oldData, columns, modalVisible, onSubmit, onCancel } = props;

  return (
    <Modal
      destroyOnClose
      title="更新个人信息"
      open={modalVisible}
      onCancel={() => onCancel()}
      footer={null}
    >
      <ProTable<API.User, API.User>
        rowKey="id"
        type="form"
        columns={columns}
        form={{
          initialValues: oldData,
        }}
        onSubmit={async (values) => {
          const success = await handleUpdate({
            ...values,
            id: oldData.id,
          });
          if (success) {
            onSubmit?.();
          }
        }}
      />
    </Modal>
  );
};

export default UpdateUserInfoModal;
