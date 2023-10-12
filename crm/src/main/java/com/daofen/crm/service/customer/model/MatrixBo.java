package com.daofen.crm.service.customer.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatrixBo {
	
	private List<Double> bMatrix;
	
	private List<List<Double>> dMatrix;
	
}
