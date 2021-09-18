package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import utils.FileUtil;
import view.MvvmDialog;

public class MvvmHelperPlugin extends AnAction {
    private Project project;


    @Override
    public void actionPerformed(AnActionEvent event) {
        project = event.getProject();
        FileUtil fileUtil =new FileUtil(event);
        MvvmDialog mvvmDialog = new MvvmDialog((editText, checkData) -> {
            fileUtil.clickCreateFile(editText,checkData);

        });
        mvvmDialog.showDialog();
    }

}
