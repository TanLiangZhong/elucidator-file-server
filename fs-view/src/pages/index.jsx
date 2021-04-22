import { request } from 'umi';
import React, { useEffect, useState } from 'react';
import { Space, Table } from 'antd';

function findPage(params) {
  return request('/api/findPage', {
    params,
    skipErrorHandler: true,
  });
}

const columns = [
  {
    title: '文件名称',
    dataIndex: 'name',
  },
  {
    title: '文件大小',
    dataIndex: 'size',
    render: (val) => {
      let size = val;
      let text = ['B', 'KB', 'MB', 'GB', 'TB'];
      let i = 0;
      while (size > 1024) {
        i++;
        size = size / 1024;
      }
      return `${size.toFixed(2)} ${text[i]}`;
    },
  },
  {
    title: '文件类型',
    dataIndex: 'contentType',
  },
  {
    title: '上传时间',
    dataIndex: 'gmtCreated',
    sorter: true,
  },
  {
    title: '操作',
    dataIndex: 'previewUrl',
    render: (val) => (<Space size='middle'>
      <a target='_blank' href={val}>下载</a>
    </Space>),
  },
];


export default function IndexPage() {
  const [pagination, setPagination] = useState({
    current: 1,
    size: 10,
  });
  const [data, setDate] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleTableChange = (pagination, filters, sorter) => {
    console.log(pagination);
    console.log(filters);
    console.log(sorter);
  };

  const getPage = () => {
    setLoading(true);
    findPage(pagination).then(r => {
      setDate(r.result.list);
      setLoading(false);
    });
  };

  useEffect(() => {
    getPage();
  }, []);

  return (
    <Table
      columns={columns}
      rowKey='fileId'
      dataSource={data}
      pagination={pagination}
      loading={loading}
      onChange={handleTableChange}
    />
  );
}
