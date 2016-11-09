package nl.milean.missionrace.missiondata;

import nl.milean.missionrace.missions.ChestFragment;
import nl.milean.missionrace.missions.MessageOnlyFragment;
import nl.milean.missionrace.missions.DistancePhotoHiddenFragment;
import nl.milean.missionrace.missions.DistanceQuestionFragment;
import nl.milean.missionrace.missions.DistanceThresholdFragment;
import nl.milean.missionrace.missions.DistanceThresholdHiddenFragment;
import nl.milean.missionrace.missions.MissionFragment;
import nl.milean.missionrace.missions.PhotoFragment;
import nl.milean.missionrace.missions.QuestionFragment;
import nl.milean.missionrace.missions.RevealFragment;
import nl.milean.missionrace.missions.TextFragment;

/**
 * Created by Michiel on 13-3-2016.
 */
public enum MissionTypes {
    DistanceThresholdHidden(DistanceThresholdHiddenFragment.class),
    DistanceThreshold(DistanceThresholdFragment.class),
    DistancePhotoHidden(DistancePhotoHiddenFragment.class),
    DistanceQuestion(DistanceQuestionFragment.class),
    Photo(PhotoFragment.class),
    Question(QuestionFragment.class),
    Reveal(RevealFragment.class),
    Text(TextFragment.class),
    MessageOnly(MessageOnlyFragment.class),
    Chests(ChestFragment.class);

    public final Class<? extends MissionFragment> implClass;

    MissionTypes(Class<? extends MissionFragment> implClass) {
        this.implClass = implClass;
    }

    public MissionFragment implFrag() {
        try {
            return this.implClass.newInstance();
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}
