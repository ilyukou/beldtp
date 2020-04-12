package org.telegram.bot.beldtp.handler.subclasses.add;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.exception.BadRequestException;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackAndRejectIncidentHandler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.repository.interf.LocationRepository;
import org.telegram.bot.beldtp.service.interf.GeoCoderService;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.AttachmentFileService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.EmojiUtil;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

@HandlerInfo(type = "addLocation", accessRight = UserRole.USER)
public class AddLocationHandler extends Handler {

    private static final String LOCATION_WAS_ADDED = "locationWasAdded";

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private AttachmentFileService attachmentFileService;

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
    public String getText(User user, Update update) {
        Incident draft = incidentService.getDraft(user);

        if (draft.getLocation() != null) {
            return answerService.get(LOCATION_WAS_ADDED, user.getLanguage()).getText()
                    + "\n\n" + draft.getLocation();
        }

        return getAnswer(user.getLanguage()).getText();
    }

    @Override
    public String getLabel(User user, Update update) {
        Incident draft = incidentService.getDraft(user);

        if (draft.getLocation() != null) {
            return EmojiUtil.CHECK_MARK_BUTTON + " " + getAnswer(user.getLanguage()).getLabel();
        }

        return EmojiUtil.WHITE_LARGE_SQUARE + " " + getAnswer(user.getLanguage()).getLabel();
    }

    @Override
    public List<TelegramResponse> handle(List<TelegramResponse> responses, User user, Update update) {

        List<TelegramResponse> transaction = transaction(responses, user, update);

        if (transaction != null) {
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

            draft = incidentService.save(draft);
            user = userService.save(user);

            return getMessage(responses, user, update);
        }

        throw new BadRequestException();
    }

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(backHandler);
    }
}
