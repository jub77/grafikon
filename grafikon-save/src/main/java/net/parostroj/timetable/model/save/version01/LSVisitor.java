package net.parostroj.timetable.model.save.version01;

public interface LSVisitor {

    void visit(LSTrainDiagram lsDiagram);

    void visit(LSNode lsNode);

    void visit(LSNodeTrack lsNodeTrack);

    void visit(LSLine lsTrack);

    void visit(LSTrain lsTrain);

    void visit(LSTimeInterval lsInterval);

    void visit(LSModelInfo lsInfo);

    void visit(LSRoute lsRoute);

    void visit(LSTrainsCycle lsCycle);

    void visit(LSImage lsImage);
}
