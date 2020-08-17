package org.code.wars;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.changes.patch.ApplyPatchFromClipboardAction;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.ide.RestService;


public class ApplyPatchService extends RestService {

  @Override
  public boolean isSupported(@NotNull FullHttpRequest request) {
    return true;
  }

  @NotNull
  @Override
  protected String getServiceName() {
    return "keymapSwitcher";
  }

  @Override
  protected boolean isMethodSupported(@NotNull HttpMethod httpMethod) {
    return httpMethod == HttpMethod.POST;
  }

  @Nullable
  @Override
  public String execute(@NotNull QueryStringDecoder queryStringDecoder, @NotNull FullHttpRequest fullHttpRequest,
                        @NotNull ChannelHandlerContext channelHandlerContext) {
    System.setProperty("idea.trusted.chrome.extension.id", "inicfikfgahabbmboppkmgopiiapdnjn");
    // may be use ProjectManager
    Project project = getLastFocusedOrOpenedProject();

    if (project != null) {
      ApplicationManager.getApplication().invokeLater(
          () -> new ApplyPatchFromClipboardAction.MyApplyPatchFromClipboardDialog(project, "Index: src/Test.java\n" +
                                                                                           "IDEA additional info:\n" +
                                                                                           "Subsystem: com.intellij.openapi.diff.impl.patch.CharsetEP\n" +
                                                                                           "<+>UTF-8\n" +
                                                                                           "===================================================================\n" +
                                                                                           "--- src/Test.java\t(date 1596695162128)\n" +
                                                                                           "+++ src/Test.java\t(date 1596695162128)\n" +
                                                                                           "@@ -0,0 +1,2 @@\n" +
                                                                                           "+public class Test {\n" +
                                                                                           "+}\n").show(),
          ModalityState.any());

      sendOk(fullHttpRequest, channelHandlerContext);
      return null;
    }

    return "No open project";
  }

  @Override
  protected boolean isHostTrusted(@NotNull FullHttpRequest request,
                                  @NotNull QueryStringDecoder urlDecoder) {
    return true;
  }
}