package org.ashe.security.infra;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ExceptionAlarm {

    private ExceptionAlarm() {
    }

    /**
     * 通知开发人员
     */
    public static void noticeDeveloper(String message, String developer, String title) {
        try {
            // 1-初始化API调用Client
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?access_token=7c99c9e8bc7743816f00815df6229e6e9712429b5521f51b521006f19a58baef");
            // 2-初始化请求参数
            OapiRobotSendRequest req = new OapiRobotSendRequest();
            // 2-1 设置消息类型
            req.setMsgtype("text");
            // 2-2 设置消息@人
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setAtMobiles(List.of(developer));
            req.setAt(at);
            // 2-3 设置消息内容(必须匹配关键词)，工作中这里就对应error日志
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            text.setContent(title + System.lineSeparator() + message);
            req.setText(text);
            // 3 消息发送
            OapiRobotSendResponse rsp = client.execute(req, "");
            log.info("钉钉消息发送成功：{}", rsp.getParams().get("text"));
        } catch (ApiException e) {
            log.error("钉钉消息发送失败：{}", e.getMessage());
        }
    }

}
