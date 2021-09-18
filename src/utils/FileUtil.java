package utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import enums.CodeType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {
    private AnActionEvent event;
    private Project project;
    private String editName;//输入的名字
    private String packageName = ""; //app项目中的包名  从manifest里读取
    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    public FileUtil(AnActionEvent event) {
        this.event = event;
        this.project = event.getProject();
        this.packageName = readPackageName();

    }

    /**
     * 开始生成文件
     *
     * @param editText
     * @param checkData
     */
    public void clickCreateFile(String editText, LinkedHashMap<String, Boolean> checkData) {
        this.editName = editText;
        boolean isJava = checkData.get("编译类型");

        if (checkData.get(CodeType.FRAGMENT.getEcode())) {
            createFile(CodeType.FRAGMENT, isJava);
        }
        if (checkData.get(CodeType.VIEWMODEL.getEcode())) {
            createFile(CodeType.VIEWMODEL, isJava);
        }
        if (checkData.get(CodeType.REPOSITORY.getEcode())) {
            createFile(CodeType.REPOSITORY, isJava);
        }
        if (checkData.get(CodeType.BEAN.getEcode())) {
            createFile(CodeType.BEAN, isJava);
        }
        if (checkData.get(CodeType.LAYOUT.getEcode())) {
            createFile(CodeType.LAYOUT, isJava);
        }


        project.getProjectFile().refresh(false, true);

    }

    private void createFile(CodeType codeType, boolean isJava) {
        //先取到模板名字
        String templateName = codeType.getEtemplate();
        if (isJava && (codeType.getEcode().equals(CodeType.FRAGMENT.getEcode()) || codeType.getEcode().equals(CodeType.VIEWMODEL.getEcode()) || codeType.getEcode().equals(CodeType.REPOSITORY.getEcode()))) {
            templateName = templateName + "Java";
        }
        templateName = templateName + ".txt";

        //再读取模板里面的内容
        String content = readTemplateFile(templateName);

        String fileName = editName;
        if (codeType.getEcode().equals(CodeType.LAYOUT.getEcode())) {
            fileName = "fragment" + humpToLine(editName);
        } else {
            content =dealTemplateContent(templateName,content);
        }

        //开始写文件
        writeToFile(content, codeType.getPackagePath(project.getBasePath(), packageName), fileName + codeType.getSuffix(isJava));

    }

    /**
     * 开始生成文件
     * @param content
     * @param classPath  路径   项目路径/app/src/main/java/项目包名/文件路径    项目路径/app/src/main/res/layout/文件路径
     * @param className  文件名字
     */
    private void writeToFile(String content, String classPath, String className) {
        try {
            File floder = new File(classPath);
            if (!floder.exists()) {
                floder.mkdirs();
            }

            File file = new File(classPath + "/" + className);
            if (!file.exists()) {
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(content);
                bw.close();
            }
        } catch (IOException e) {
            toast("生产文件错误");
        }

    }

    /**
     * 再处理模板中的内容
     *
     * @param templateName 模板名字
     * @param content
     * @return
     */
    private String dealTemplateContent(String templateName, String content) {

        content = content.replace("$name", editName); //注意 这个地方可能把$nameRepository 或者$nameViewModel  也替换了
        content = content.replace("$date", getDate());

        if (content.contains("$packagename")) {
            content = content.replace("$packagename", this.packageName);
        }
        if (content.contains("$fragmentName")) {
            content = content.replace("$fragmentName", CodeType.FRAGMENT.getPackageName(this.packageName));
        }

        if (content.contains("$importModel")) {
            //先判断文件是否是ViewModel
            if (templateName.contains("ViewModel")) {
                content = content.replace("$importModel", CodeType.VIEWMODEL.getPackageName(this.packageName) + "." + editName + "Model");
            } else {
                content = content.replace("$importModel", "com.gh.comm.library.live.BaseViewModel");
            }
        }

        if (content.contains("$baseModel")) {
            if (templateName.contains("ViewModel")) {
                content = content.replace("$baseModel", editName + "ViewModel");
            } else {
                content = content.replace("$baseModel", "BaseViewModel");
            }
        }

        if (content.contains("$modelName")) {
            content = content.replace("$modelName", CodeType.VIEWMODEL.getPackageName(this.packageName));
        }
        // $repositoryName repository的路径名
        if (content.contains("$repositoryName")) {
            content = content.replace("$repositoryName", CodeType.REPOSITORY.getPackageName(this.packageName));
        }


        if (content.contains("$beanName")) {
            content = content.replace("$beanName", CodeType.BEAN.getPackageName(this.packageName));
        }

        if (content.contains("$layoutId")) {
            if (templateName.contains("Layout")) {
                content = content.replace("$layoutId", " R.layout.fragment" + humpToLine(editName));
            } else {
                content = content.replace("$layoutId", " 0");
            }
        }

        return content;
    }


    /**
     * 读取模板中的内容
     * @param fileName
     * @return
     */
    public String readTemplateFile(String fileName)  {
        InputStream in = this.getClass().getResourceAsStream("/templates/"+fileName);
        String content = "";
        try {
            content = new String(readStream(in));
        } catch (Exception e) {
            toast(e.toString());
        }
        return content;
    }

    private byte[] readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
                System.out.println(new String(buffer));
            }

        } catch (IOException e) {
        } finally {
            outSteam.close();
            inputStream.close();
        }
        return outSteam.toByteArray();
    }


    /**
     * 读取项目的报名
     *
     * @return
     */
    private String readPackageName() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(project.getBasePath() + "/App/src/main/AndroidManifest.xml");

            NodeList dogList = doc.getElementsByTagName("manifest");
            for (int i = 0; i < dogList.getLength(); i++) {
                Node dog = dogList.item(i);
                Element elem = (Element) dog;
                return elem.getAttribute("package");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void toast(String msg) {
        Messages.showInfoMessage(this.project, msg, "提示");
    }

    /**
     * 获取当前的日期
     * @return
     */
    public String getDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 名字小写并加上——
     * @param str
     * @return
     */
    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();

        while(matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }

        matcher.appendTail(sb);
        return sb.toString();
    }
}
