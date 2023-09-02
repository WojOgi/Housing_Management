package com.example.housingmanagement.api.requests;

public class AssignmentRequest {

    private HouseRequest houseToAssign;
    private OccupantRequest occupantToAssign;

    public AssignmentRequest(HouseRequest houseToAssign, OccupantRequest occupantToAssign) {
        this.houseToAssign = houseToAssign;
        this.occupantToAssign = occupantToAssign;
    }

    public HouseRequest getHouseToAssign() {
        return houseToAssign;
    }

    public void setHouseToAssign(HouseRequest houseToAssign) {
        this.houseToAssign = houseToAssign;
    }

    public OccupantRequest getOccupantToAssign() {
        return occupantToAssign;
    }

    public void setOccupantToAssign(OccupantRequest occupantToAssign) {
        this.occupantToAssign = occupantToAssign;
    }
}
