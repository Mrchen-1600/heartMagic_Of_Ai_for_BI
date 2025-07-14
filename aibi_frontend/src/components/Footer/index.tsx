import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';

const Footer: React.FC = () => {
  const defaultMessage = 'by尘小风';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: '心灵魔方智能 BI',
          title: '心灵魔方智能 BI',
          href: 'https://github.com/Mrchen-1600',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/Mrchen-1600',
          blankTarget: true,
        },
        {
          key: '心灵魔方智能 BI',
          title: '心灵魔方智能 BI',
          href: 'https://github.com/Mrchen-1600',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
