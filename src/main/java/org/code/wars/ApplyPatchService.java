package org.code.wars;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import java.time.Instant;


public class ApplyPatchService extends RestService {

  static {
    System.setProperty("idea.trusted.chrome.extension.id", "inicfikfgahabbmboppkmgopiiapdnjn");
  }

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
    final JsonObject jsonObject = new JsonParser().parse(createJsonReader(fullHttpRequest)).getAsJsonObject();

    String setup = jsonObject.get("setup").toString()
            .replace("\"", "")
            .replace("\\t", "    ")
            .replace("\\n", "\n+");

    ApplicationManager.getApplication().invokeLater(
            () -> Messages.showMessageDialog(setup, "Json Class", null),
            ModalityState.any());

    String className = getClassName(setup);

    // may be use ProjectManager
    Project project = getLastFocusedOrOpenedProject();

    if (project != null) {
      ApplicationManager.getApplication().invokeLater(
              () -> {
                long epochMilli = Instant.now().toEpochMilli();
                new ApplyPatchFromClipboardAction.MyApplyPatchFromClipboardDialog(project, "Index: src/Test.java\n" +
                        "IDEA additional info:\n" +
                        "Subsystem: com.intellij.openapi.diff.impl.patch.CharsetEP\n" +
                        "<+>UTF-8\n" +
                        "===================================================================\n" +
                        "--- src/" + className + ".java\t(date " + epochMilli + ")\n" +
                        "+++ src/" + className + ".java\t(date " + epochMilli + ")\n" +
                        "@@ -0,0 +1,100 @@\n" +
                        "+" + setup).show();
              },
              ModalityState.any());

      sendOk(fullHttpRequest, channelHandlerContext);
      return null;
    }

    return "No open project";
  }

  private String getClassName(String setup) {
    return setup.split("public class ")[1]
            .split(" ")[0];
  }

  @Override
  protected boolean isHostTrusted(@NotNull FullHttpRequest request,
                                  @NotNull QueryStringDecoder urlDecoder) {
    return true;
  }

}