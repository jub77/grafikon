/*
 * CreateTrainCommand.java
 *
 * Created on 4.9.2007, 9:24:24
 */
package net.parostroj.timetable.gui.commands;

import net.parostroj.timetable.actions.TrainBuilder;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;

/**
 * Command for creating trains.
 *
 * @author jub
 */
public class CreateTrainCommand implements Command {

    private final String number;
    private final TrainType type;
    private final Integer topSpeed;
    private final Route route;
    private final int time;
    private final int defaultStop;
    private final String description;
    private final boolean diesel;
    private final boolean electric;
    private final boolean showLength;
    private final Group group;
    private final boolean managedFreight;

    /**
     * creates instance of create train command.
     *
     * @param number number of the train
     * @param type train type
     * @param topSpeed maximum speed of the train
     * @param route route
     * @param time starting time
     * @param defaultStop default stop
     * @param description description
     * @param diesel if the train is electric
     * @param electric if the train is diesel
     * @param showLength show max. length in timetable
     * @param group group
     * @param managedFreight managed freight
     */
    public CreateTrainCommand(String number, TrainType type, Integer topSpeed, Route route, int time, int defaultStop, String description, boolean diesel, boolean electric, boolean showLength, Group group, boolean managedFreight) {
        this.number = number;
        this.type = type;
        this.topSpeed = topSpeed;
        this.route = route;
        this.time = time;
        this.defaultStop = defaultStop;
        this.description = description;
        this.diesel = diesel;
        this.electric = electric;
        this.showLength = showLength;
        this.group = group;
        this.managedFreight = managedFreight;
    }

    @Override
    public void accept(ApplicationModel model) {
        TrainBuilder trainBuilder = new TrainBuilder();

        Train train;
        try {
            train = trainBuilder.createTrain(IdGenerator.getInstance().getId(), number, type, topSpeed, route, time, model.getDiagram(), defaultStop);
        } catch (Exception e) {
            throw new GrafikonException(e.getMessage(), e);
        }

        train.setAttribute(Train.ATTR_DIESEL, diesel);
        train.setAttribute(Train.ATTR_ELECTRIC, electric);
        train.setDescription(description);
        if (train.getType() != null && train.getType().getCategory() != null && !train.getType().getCategory().getKey().equals("freight")) {
            train.setAttribute(Train.ATTR_EMPTY, Boolean.TRUE);
        }
        if (showLength) {
            train.setAttribute(Train.ATTR_SHOW_STATION_LENGTH, Boolean.TRUE);
        }
        if (group != null) {
            train.setAttribute(Train.ATTR_GROUP, group);
        }
        if (managedFreight) {
            train.setAttribute(Train.ATTR_MANAGED_FREIGHT, managedFreight);
        }

        // add train to diagram
        model.getDiagram().getTrains().add(train);
    }
}
