package org.code.wars;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.changes.patch.ApplyPatchFromClipboardAction;
import org.jetbrains.annotations.NotNull;

public class MyDemoAction extends ApplyPatchFromClipboardAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Messages.showMessageDialog("Test", "Test", null);
    super.actionPerformed(e);
  }
}
