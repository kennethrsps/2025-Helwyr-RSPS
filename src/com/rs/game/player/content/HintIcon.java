package com.rs.game.player.content;

public final class HintIcon {

    private int coordX;
    private int coordY;
    private int plane;
    private int distanceFromFloor;
    private int targetType;
    private int targetIndex;
    private int arrowType;
    private int modelId;
    private int index;

    public HintIcon() {
	this.setIndex(7);
    }

    public HintIcon(int targetType, int modelId, int index) {
	this.setTargetType(targetType);
	this.setModelId(modelId);
	this.setIndex(index);
    }

    public HintIcon(int targetIndex, int targetType, int arrowType,
	    int modelId, int index) {
	this.setTargetType(targetType);
	this.setTargetIndex(targetIndex);
	this.setArrowType(arrowType);
	this.setModelId(modelId);
	this.setIndex(index);
    }

    public HintIcon(int coordX, int coordY, int height, int distanceFromFloor,
	    int targetType, int arrowType, int modelId, int index) {
	this.setCoordX(coordX);
	this.setCoordY(coordY);
	this.setPlane(height);
	this.setDistanceFromFloor(distanceFromFloor);
	this.setTargetType(targetType);
	this.setArrowType(arrowType);
	this.setModelId(modelId);
	this.setIndex(index);
    }

    public int getArrowType() {
	return arrowType;
    }

    public int getCoordX() {
	return coordX;
    }

    public int getCoordY() {
	return coordY;
    }

    public int getDistanceFromFloor() {
	return distanceFromFloor;
    }

    public int getIndex() {
	return index;
    }

    public int getModelId() {
	return modelId;
    }

    public int getPlane() {
	return plane;
    }

    public int getTargetIndex() {
	return targetIndex;
    }

    public int getTargetType() {
	return targetType;
    }

    public void setArrowType(int arrowType) {
	this.arrowType = arrowType;
    }

    public void setCoordX(int coordX) {
	this.coordX = coordX;
    }

    public void setCoordY(int coordY) {
	this.coordY = coordY;
    }

    public void setDistanceFromFloor(int distanceFromFloor) {
	this.distanceFromFloor = distanceFromFloor;
    }

    public void setIndex(int modelPart) {
	this.index = modelPart;
    }

    public void setModelId(int modelId) {
	this.modelId = modelId;
    }

    public void setPlane(int plane) {
	this.plane = plane;
    }

    public void setTargetIndex(int targetIndex) {
	this.targetIndex = targetIndex;
    }

    public void setTargetType(int targetType) {
	this.targetType = targetType;
    }
}
