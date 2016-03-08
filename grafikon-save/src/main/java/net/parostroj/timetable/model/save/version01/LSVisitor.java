package net.parostroj.timetable.model.save.version01;

public interface LSVisitor {

    public void visit(LSTrainDiagram lsDiagram);

    public void visit(LSNode lsNode);

    public void visit(LSNodeTrack lsNodeTrack);

    public void visit(LSLine lsTrack);

    public void visit(LSTrain lsTrain);

    public void visit(LSTimeInterval lsInterval);
    
    public void visit(LSModelInfo lsInfo);
    
    public void visit(LSRoute lsRoute);
    
    public void visit(LSTrainsCycle lsCycle);
    
    public void visit(LSImage lsImage);
}
