package org.code.wars;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
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

  private static final String CLASS_PREFIX = "public class ";
  private static final String SPACE = " ";
  private static final String CLASS_KEY_IN_JSON = "setup";

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
    final JsonObject jsonObject = JsonParser.parseReader(createJsonReader(fullHttpRequest))
            .getAsJsonObject();

    String jsonTestClass = prepareJson(jsonObject, "exampleFixture");
    String jsonClass = prepareJson(jsonObject, CLASS_KEY_IN_JSON);

    // may be use ProjectManager
    Project project = getLastFocusedOrOpenedProject();

    if (project != null) {
      ApplicationManager.getApplication().invokeLater(
              () -> {
                String clipboardText = getClipboardText(jsonClass) + "\n" +getClipboardText(jsonTestClass);
                new ApplyPatchFromClipboardAction.MyApplyPatchFromClipboardDialog(project, clipboardText).show();
              },
              ModalityState.any());

      sendOk(fullHttpRequest, channelHandlerContext);
      return null;
    }

    return "No open project";
  }

  @NotNull
  private String getClipboardText(String jsonClass) {
    String className = getClassName(jsonClass);
    long epochMilli = Instant.now().toEpochMilli();

    return  "IDEA additional info:\n" +
            "Subsystem: com.intellij.openapi.diff.impl.patch.CharsetEP\n" +
            "<+>UTF-8\n" +
            "===================================================================\n" +
            "--- src/" + className + ".java\t(date " + epochMilli + ")\n" +
            "+++ src/" + className + ".java\t(date " + epochMilli + ")\n" +
            "@@ -0,0 +1,100 @@\n" +
            "+" + jsonClass;
  }

  @NotNull
  private String prepareJson(JsonObject jsonObject, String jsonElement) {
    return jsonObject.get(jsonElement).getAsString()
            .replace("\n", "\n ");
  }

  private String getClassName(String setup) {
    return setup.split(CLASS_PREFIX)[1]
            .split(SPACE)[0];
  }

  @Override
  protected boolean isHostTrusted(@NotNull FullHttpRequest request,
                                  @NotNull QueryStringDecoder urlDecoder) {
    return true;
  }

}