package org.telegram.bot.beldtp.handler.subclasses.add;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.AddHandler;
import org.telegram.bot.beldtp.handler.subclasses.MainHandler;
import org.telegram.bot.beldtp.listener.telegramResponse.TelegramResponseBlockingQueue;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@HandlerInfo(type = "confirm", accessRight = UserRole.USER)
public class ConfirmAddHandler extends Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmAddHandler.class);

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Autowired
    private AddTimeHandler addTimeHandler;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private MainHandler mainHandler;

    @Autowired
    private AddHandler addHandler;

    @Override
    public TelegramResponse getMessage(User user, Update update) {
        Incident incident = incidentService.getDraft(user);

        return getIncident(incident,user);
    }

    @Override
    public TelegramResponse handle(User user, Update update) {

        Incident draft = incidentService.getDraft(user);

        while (!user.peekStatus().equals(mainHandler.getType())){
            user.popStatus();
        }

        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();

        if(update.getCallbackQuery().getData().equals("Confirm")){

            answerCallbackQuery.setText(answerService.get(getType()+"Build",
                    user.getLanguage()).getText());
            // "You incident build."
            answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());

            draft.setType(IncidentType.BUILD);
            draft = incidentService.save(draft);

            user = userService.save(user);

        }else if(update.getCallbackQuery().getData().equals("Reject")){
            answerCallbackQuery.setText("Reject");
            answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());

            user.remove(draft);
            user = userService.save(user);

            incidentService.delete(draft);

        } else {
            return null;
        }
        telegramResponseBlockingQueue.push(new TelegramResponse(answerCallbackQuery));
        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    public TelegramResponse getIncident(Incident incident, User user){
        SendMessage sendMessage = new SendMessage();
        List<InlineKeyboardButton> buttons = new LinkedList<>();
        buttons.add(new InlineKeyboardButton().setText("Confirm").setCallbackData("Confirm"));
        buttons.add(new InlineKeyboardButton().setText("Reject").setCallbackData("Reject"));

        sendMessage.setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(Collections.singletonList(buttons)));
        sendMessage.setText(getIncidentAsString(incident));
        sendMessage.setChatId(user.getId());
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        return new TelegramResponse(sendMessage);
    }

    public String getIncidentAsString(Incident incident){
        StringBuilder builder = new StringBuilder();

        int photoSize = 0;
        int videoSize = 0;

        for (Media media : incident.getMedia()){
            if(media.getMediaType() == MediaType.PHOTO){
                photoSize++;
            }

            if(media.getMediaType() == MediaType.VIDEO){
                videoSize++;
            }
        }

        if(photoSize > 0){
            builder.append("\uD83D\uDDBC ").append(photoSize).append("\n");
        }

        if(videoSize > 0){
            builder.append("\uD83D\uDCF9 ").append(videoSize)
                    .append("\n")
                    .append("\n");
        }

        builder.append(incident.getText()).append("\n")
                .append("\n")
                .append(incident.getLocation().toString());

        return builder.toString();
    }

    public TelegramResponse getSendMediaGroup(Incident incident, User user){
        SendMediaGroup sendMediaGroup = incidentService.getSendMediaGroup(incident);
        sendMediaGroup.setChatId(user.getId());

        return new TelegramResponse(sendMediaGroup);
    }
}

//if (incident.getMedia() == null || incident.getMedia().size() == 0) {
//        user.popStatus();
//        user = userService.save(user);
//
//        return new TelegramResponse(
//        new AnswerCallbackQuery()
//        .setCallbackQueryId(update.getCallbackQuery().getId())
//        .setText(answerService.get(getType()+"RequiredParameterMedia",
//        user.getLanguage()).getText()));
//        }
//
//        if (incident.getLocation().getLatitude() == null || incident.getLocation().getLongitude() == null) {
//        user.popStatus();
//        user = userService.save(user);
//
//        return new TelegramResponse(
//        new AnswerCallbackQuery()
//        .setCallbackQueryId(update.getCallbackQuery().getId())
//        .setText(answerService.get(getType()+"RequiredParameterLocation",
//        user.getLanguage()).getText()));
//        }
//
//        if (incident.getText() == null) {
//        user.popStatus();
//        user = userService.save(user);
//
//        return new TelegramResponse(
//        new AnswerCallbackQuery()
//        .setCallbackQueryId(update.getCallbackQuery().getId())
//        .setText(answerService.get(getType()+"RequiredParameterText",
//        user.getLanguage()).getText()));
//        }
//
//        if (incident.getTime() == null) {
//        user.pushStatus(addTimeHandler.getType());
//
//        user = userService.save(user);
//
//        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
//        }