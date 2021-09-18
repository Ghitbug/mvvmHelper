package view;

import enums.CodeType;
import listener.OnCreatFileListner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class MvvmDialog extends JDialog implements ActionListener {

    private String[] titles = {"是否创建activity", "是否创建fragment", "是否创建viewmodel", "是否创建repository", "是否创建bean", "是否创建layout", "编译类型"};
    //选择的数据
    private LinkedHashMap<String, ArrayList<JRadioButton>> checkRadioBtn = new LinkedHashMap();
    // GridBagLayout不要求组件的大小相同便可以将组件垂直、水平或沿它们的基线对齐
    private GridBagLayout mLayout = new GridBagLayout();
    // GridBagConstraints用来控制添加进的组件的显示位置
    private GridBagConstraints mConstraints = new GridBagConstraints();
    //输入框的Panel
    private JPanel panelTitle = new JPanel();
    private JTextField jTextField = new JTextField(25);
    //中间要显示的Panel
    private JPanel contentPanel = new JPanel();
    private GridBagConstraints mContentConstraints = new GridBagConstraints();
    private GridBagLayout mContentLayout = new GridBagLayout();

    // 确定、取消JPanel
    private JPanel bottomPanel = new JPanel();
    private JButton mButtonConfirm = new JButton("确定");
    private JButton mButtonCancel = new JButton("取消");

    private Container container = getContentPane();

    private OnCreatFileListner onCreatFileListner;

    public MvvmDialog(OnCreatFileListner onCreatFileListner) {
        this.onCreatFileListner = onCreatFileListner;
        initView();

    }

    private void initView() {
        initTopTitle();
        initContent();
        initBottom();
        setConstraints();
        setDialog();
    }


    /**
     * 设置dialog的属性
     */
    private void setDialog() {
        this.setModal(true);
        // 设置标题
        this.setTitle("mvvm快速创建");
        // 自适应大小
        //pack();
        this.setSize(400, 500);
        // 设置布局管理
        setLayout(mLayout);
        // 设置居中，放在setSize后面
        this.setLocationRelativeTo(null);
        // 不可拉伸
        setResizable(false);
        // 显示最前
        setAlwaysOnTop(true);
    }

    /**
     * 设置每个容器panel的位置
     */
    private void setConstraints() {
        // 使组件完全填满其显示区域
        mConstraints.fill = GridBagConstraints.BOTH;
        // 设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        mConstraints.gridwidth = 0;
        // 第几列
        mConstraints.gridx = 0;
        // 第几行
        mConstraints.gridy = 0;
        // 行拉伸0不拉伸，1完全拉伸
        mConstraints.weightx = 1;
        // 列拉伸0不拉伸，1完全拉伸
        mConstraints.weighty = 0;
        // 设置组件
        mLayout.setConstraints(panelTitle, mConstraints);


        mConstraints.fill = GridBagConstraints.BOTH;
        mConstraints.gridwidth = 1;
        mConstraints.gridx = 0;
        mConstraints.gridy = 1;
        mConstraints.weightx = 1;
        mConstraints.weighty = 1;
        mLayout.setConstraints(contentPanel, mConstraints);

        mConstraints.fill = GridBagConstraints.BOTH;
        mConstraints.gridwidth = 0;
        mConstraints.gridx = 0;
        mConstraints.gridy = 2;
        mConstraints.weightx = 1;
        mConstraints.weighty = 0;
        mConstraints.anchor = GridBagConstraints.EAST;
        mLayout.setConstraints(bottomPanel, mConstraints);


    }

    /**
     * 确定和取消
     */
    private void initBottom() {
        bottomPanel.setLayout(new GridLayout(1, 2));// 定义排版，一行2列
        // 添加监听
        mButtonConfirm.addActionListener(this);
        mButtonCancel.addActionListener(this);
        // 右边
        bottomPanel.add(mButtonConfirm);
        bottomPanel.add(mButtonCancel);
        // 添加到JFrame
        container.add(bottomPanel);
    }

    /**
     * item布局 添加选项
     */
    private void initContent() {

        for (int i = 0; i < titles.length; i++) {

            JPanel itemJPanel = new JPanel();
            itemJPanel.setLayout(new GridLayout(1, 3));
            itemJPanel.setBorder(new EmptyBorder(20, 10, 5, 20));
            itemJPanel.add(new JLabel(titles[i]));
            getButtonGroup(itemJPanel, titles[i]);
            contentPanel.add(itemJPanel);

            mContentConstraints.fill = GridBagConstraints.HORIZONTAL;
            mContentConstraints.gridwidth = 0;
            mContentConstraints.gridx = 0;
            mContentConstraints.gridy = i;
            mContentConstraints.weightx = 1;
            mContentLayout.setConstraints(itemJPanel, mContentConstraints);
        }
        contentPanel.setLayout(mContentLayout);
        container.add(contentPanel);
    }

    /**
     * 顶部输入框
     */
    private void initTopTitle() {

        panelTitle.add(new JLabel("文件名称"));
        panelTitle.add(jTextField);
        container.add(panelTitle);
    }

    /**
     * 创建按钮组，把两个单选按钮添加到该组
     *
     * @return
     */
    private ButtonGroup getButtonGroup(JPanel panel, String title) {
        ButtonGroup btnGroup = new ButtonGroup();
        JRadioButton radioBtn01 = null;
        JRadioButton radioBtn02 = null;
        if ("编译类型".equals(title)) {
            radioBtn01 = new JRadioButton("java");
            radioBtn02 = new JRadioButton("kt");
        } else {
            radioBtn01 = new JRadioButton("是");
            radioBtn02 = new JRadioButton("否");
        }

        radioBtn01.setSelected(true);
        radioBtn01.setHorizontalAlignment(SwingConstants.CENTER);
        btnGroup.add(radioBtn01);
        panel.add(radioBtn01);

        radioBtn02.setHorizontalAlignment(SwingConstants.RIGHT);
        btnGroup.add(radioBtn02);
        panel.add(radioBtn02);

        checkRadioBtn.put(title, new ArrayList<JRadioButton>(Arrays.asList(radioBtn01, radioBtn02)));

        return btnGroup;
    }

    /**
     * 显示dialog
     */
    public void showDialog() {
        // 显示
        setVisible(true);
    }

    /**
     * 关闭dialog
     */
    public void cancelDialog() {
        setVisible(false);
        dispose();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if ("取消".equals(e.getActionCommand())) {
            this.dispose();
        } else {
            this.dispose();
            LinkedHashMap<String, Boolean> map = new LinkedHashMap();
            for (String key : checkRadioBtn.keySet()) {
                ArrayList<JRadioButton> value = checkRadioBtn.get(key);
                switch (key) {
                    case "是否创建activity":
                        map.put(CodeType.ACTIVITY.getEcode(), value.get(0).isSelected());
                        break;
                    case "是否创建fragment":
                        map.put(CodeType.FRAGMENT.getEcode(), value.get(0).isSelected());
                        break;
                    case "是否创建viewmodel":
                        map.put(CodeType.VIEWMODEL.getEcode(), value.get(0).isSelected());
                        break;
                    case "是否创建repository":
                        map.put(CodeType.REPOSITORY.getEcode(), value.get(0).isSelected());
                        break;
                    case "是否创建bean":
                        map.put(CodeType.BEAN.getEcode(), value.get(0).isSelected());
                        break;
                    case "是否创建layout":
                        map.put(CodeType.LAYOUT.getEcode(), value.get(0).isSelected());
                        break;
                    case "编译类型":
                        map.put(key, value.get(0).isSelected());
                        break;
                }

            }
            onCreatFileListner.onOnCreatFile(jTextField.getText(), map);

        }
    }

}
