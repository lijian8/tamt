package org.worldbank.transport.tamt.server.api;

import org.worldbank.transport.tamt.server.bo.AssignStatusBO;
import org.worldbank.transport.tamt.shared.AssignStatus;

public class AssignStatusAPI {

	private AssignStatusBO bo;
	
	public AssignStatusAPI()
	{
		bo = AssignStatusBO.get();
	}
	
	public AssignStatus getAssignStatusInProcess() throws Exception
	{
		return bo.getAssignStatusInProcess();
	}
	
	public AssignStatus getAssignStatus(AssignStatus status) throws Exception
	{
		return bo.getAssignStatus(status);
	}
	
	public void updateAssignStatus(AssignStatus status) throws Exception
	{
		bo.updateAssignStatus(status);
	}
	
	public void insertAssignStatus(AssignStatus status) throws Exception
	{
		bo.insertAssignStatus(status);
	}

	public void deleteStatus(AssignStatus status) throws Exception {
		bo.deleteStatus(status);
	}
	
}
