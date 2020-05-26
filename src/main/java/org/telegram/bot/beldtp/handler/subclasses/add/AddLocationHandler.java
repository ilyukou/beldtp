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
    public List<TelegramResponse> handle(User user, Update update) {

        List<TelegramResponse> transaction = transaction(user, update);

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

            return getMessage(user, update);
        }

        if(update.hasMessage() && update.getMessage().hasText()){

            try {
                if(isStringHasLocationCoordinates(update.getMessage().getText())){
                    Incident draft = incidentService.getDraft(user);

                    // latitude, longitude
                    // 53.694086, 23.810653
                    String[] coordinates = update.getMessage().getText().split(",");

                    Float latitude = Float.parseFloat(coordinates[0]);
                    Float longitude = Float.parseFloat(coordinates[1]);

                    Location location = geoCoderService.parse(longitude, latitude);


                    location.setIncident(draft);
                    location = locationRepository.save(location);

                    draft.setLocation(location);

                    draft = incidentService.save(draft);
                    user = userService.save(user);

                    return getMessage(user, update);
                }
            } catch (Exception e) {
                // ignore
                // in below code handler will be throw exception to upper level
            }
        }

        throw new BadRequestException();
    }

    boolean isStringHasLocationCoordinates(String text){
        // latitude, longitude
        // 53.694086, 23.810653
        String[] coordinates = text.split(",");

        if(coordinates.length != 2){
            return false;
        }

        try {
            Float latitude = Float.parseFloat(coordinates[0]);
            Float longitude = Float.parseFloat(coordinates[1]);
        } catch (Exception e){
            return false;
        }

        return true;
    }

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(backHandler);
    }
}
