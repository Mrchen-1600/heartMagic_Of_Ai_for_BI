import React from "react";
import MonacoEditor from "react-monaco-editor/lib/editor";

interface Props{
  value: string;//图表数据
  language?: string; //语言
  height?: number;
  onChange?: (newValue: string) => void; //操作代码编辑器后调用
}

/**
 * 代码编辑器
 */
const CodeEditor: React.FC<Props> = (props) => {
  const { value, height = 480, language = 'json', onChange } = props;

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const editorDidMount = (editor: any, monaco: any) => {
    editor.focus();
    editor.getAction('editor.action.formatDocument').run();  //格式化
  }

  const options = {
    selectOnLineNumbers: true,
    fontSize: 14,
    formatOnPaste: true,
    automaticLayout: true,
    autoIndent: true,
    minimap: {
      enabled: false,
    },
  };

  return (
    <MonacoEditor
      height={ height }
      language={ language }
      theme="vs-light"
      value={ value }
      options={ options }
      onChange={ onChange }
      editorDidMount={ editorDidMount }
    />
  );
};

export default CodeEditor;
