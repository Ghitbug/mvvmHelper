package enums;

public enum CodeType {
    ACTIVITY("ACTIVITY", "ui.activitys", "TemplateActivity", "Activity.kt"),
    FRAGMENT("FRAGMENT", "ui.fragments", "TemplateFragment", "Fragment.kt"),
    VIEWMODEL("VIEWMODEL", "mvvm.model", "TemplateViewModel", "ViewModel.kt"),
    REPOSITORY("REPOSITORY", "mvvm.repository", "TemplateRepository", "Repository.kt"),
    BEAN("BEAN", "mvvm.beans", "TemplateBean", "Bean.java"),
    LAYOUT("LAYOUT", "", "TemplateLayout", ".xml");


    private String ecode;
    private String epath;
    private String etemplate;
    private String suffix;

    private CodeType(String ecode, String epath, String etemplate, String suffix) {
        this.ecode = ecode;
        this.epath = epath;
        this.etemplate = etemplate;
        this.suffix = suffix;
    }

    public String getEcode() {
        return this.ecode;
    }

    public String getEpath() {
        return this.epath;
    }

    public String getEtemplate() {
        return this.etemplate;
    }

    public String getSuffix(boolean isJava) {
        return isJava ? this.suffix.replace("kt", "java") : this.suffix;
    }

    public String getPackageName(String packageName) {
        return packageName + "." + this.epath;
    }

    public String getPackagePath(String basePath, String packageName) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(basePath);
        stringBuffer.append("/app/src/main/");
        if (this.ecode.equals(LAYOUT.getEcode())) {
            stringBuffer.append("res/layout/");
        } else {
            stringBuffer.append("java/");
            stringBuffer.append(packageName);
            stringBuffer.append("/");
            stringBuffer.append(this.epath);
        }

        String packagePath = stringBuffer.toString().replace(".", "/");
        return packagePath;
    }
}
