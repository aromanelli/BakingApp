package info.romanelli.udacity.bakingapp.event;

import info.romanelli.udacity.bakingapp.data.StepData;

@SuppressWarnings("WeakerAccess")
public class StepDataEvent {

    public enum Type {
        SELECTED
    }

    private Type type;
    private StepData stepData;
    private int indexStepData;

    public StepDataEvent(Type type, int indexStepData, StepData stepData) {
        this.stepData = stepData;
        setType(type);
        setIndexStepData(indexStepData);
        setStepData(stepData);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public StepData getStepData() {
        return stepData;
    }

    public void setStepData(StepData stepData) {
        this.stepData = stepData;
    }

    public int getIndexStepData() {
        return indexStepData;
    }

    public void setIndexStepData(int indexStepData) {
        this.indexStepData = indexStepData;
    }

    @Override
    public String toString() {
        return "StepDataEvent{" +
                "type=" + type +
                ", indexStepData=" + indexStepData +
                ", stepData=" + stepData +
                '}';
    }

}
