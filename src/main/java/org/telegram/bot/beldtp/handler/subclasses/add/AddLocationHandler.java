package org.telegram.bot.beldtp.handler.subclasses.add;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackAndRejectIncidentHandler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.repository.interf.LocationRepository;
import org.telegram.bot.beldtp.service.interf.GeoCoderService;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.MediaService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

@HandlerInfo(type = "addLocation", accessRight = UserRole.USER)
public class AddLocationHandler extends Handler {

    private static final String REQUIRED_LOCATION = "requiredLocation";

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private AddTimeHandler addTimeHandler;

    @Autowired
    private ConfirmAddHandler confirmAddHandler;

    @Autowired
    private BackHandler backHandler;

    @Autowired
    private BackAndRejectIncidentHandler backAndRejectIncidentHandler;

    @Autowired
    private GeoCoderService geoCoderService;

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public TelegramResponse handle(User user, Update update) {

        TelegramResponse transaction = transaction(user,update);

        if(transaction != null){
            return transaction;
        }

        if(update.hasMessage() && update.getMessage().hasLocation()){
            Incident draft = incidentService.getDraft(user);

            Location location = geoCoderService.parse(
                    update.getMessage().getLocation().getLongitude(),
                    update.getMessage().getLocation().getLatitude());


            location.setIncident(draft);
            location = locationRepository.save(location);

            draft.setLocation(location);

            if(user.peekStatus().equals(getType())){
                user.popStatus();
            }

            user.pushStatus(confirmAddHandler.getType());

            draft = incidentService.save(draft);
            user = userService.save(user);

            return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
        }

        return getMessageWhenMediaHasNotText(user.getLanguage(),update);
    }

    private TelegramResponse getMessageWhenMediaHasNotText(Language language, Update update) {
        return new TelegramResponse(
                new AnswerCallbackQuery()
                        .setText(answerService.get(REQUIRED_LOCATION,language).getText())
                        .setCallbackQueryId(update.getCallbackQuery().getId())
        );
    }

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(backHandler,backAndRejectIncidentHandler);
    }
}
