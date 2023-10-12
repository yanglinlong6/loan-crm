package com.daofen.crm.service.customer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.customer.dao.CustomerMapper;
import com.daofen.crm.service.customer.model.CustomerPO;
import com.daofen.crm.service.customer.model.MatrixBo;
import com.daofen.crm.service.customer.model.UncontactedVO;

@Component
public class ICustomerService implements CustomerService,InitializingBean{
	
	private static final Logger LOG = LoggerFactory.getLogger(ICustomerService.class);

	@Autowired
	private CustomerMapper customerMapper;
	
	@Override
	public PageVO<CustomerPO> getCustomerList(PageVO<CustomerPO> vo) {
		vo.getParam().setStartDate(getTime(vo.getParam().getStartDate()," 00:00:00"));
		vo.getParam().setEndDate(getTime(vo.getParam().getEndDate()," 23:59:59"));
		if(vo.getParam().getDateType()==null) {
			vo.setData(customerMapper.getCustomerList(vo));
			vo.setTotalCount(customerMapper.getCustomerListCount(vo));
		}else if(vo.getParam().getDateType() ==1) {
			vo.setData(customerMapper.getCustomerList1(vo));
			vo.setTotalCount(customerMapper.getCustomerListCount1(vo));
		}else if(vo.getParam().getDateType() ==2) {
			vo.setData(customerMapper.getCustomerList2(vo));
			vo.setTotalCount(customerMapper.getCustomerListCount2(vo));
		}
		return vo;
	}
	
	
	
	private String getTime(String date,String priex) {
		if(StringUtils.isEmpty(date)) {
			return null;
		}
		date = date.replace("Z", " UTC");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
		SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
		    Date time = format.parse(date);
		    return defaultFormat.format(time)+priex;
		} catch (Exception e) {
			if(date.length()>10) {
				return date;
			}else {
				return date+priex;
			}
		}
	}
	
	@Override
	public void addCustomer(CustomerPO po) {
		customerMapper.addCustomer(po);
	}

	@Override
	public void updateCustomer(CustomerPO po) {
		customerMapper.updateCustomer(po);
	}

	@Override
	public void delCustomer(Long id) {
		customerMapper.delCustomer(id);
	}

	@Override
	public List<UncontactedVO> getUncontactedUsersNum(Long id) {
		return customerMapper.getUncontactedUsersNum(id);
	}

	@Override
	public List<UncontactedVO> getUncontactedTeamUsersNum(JSONObject param) {
		return customerMapper.getUncontactedTeamUsersNum(param);
	}

	@Override
	public List<JSONObject> selMediaReport(JSONObject params) {
		return customerMapper.selMediaReport(params);
	}

	@Override
	public List<JSONObject> selCustReport(JSONObject params) {
		return customerMapper.selCustReport(params);
	}

	@Override
	public List<Double> getMatrix(List<JSONObject> matrix) {
		List<Double> result = new ArrayList<Double>();
		double[][] dm = new double[6][6];
		for(int i =0;i<matrix.size();i++) {
			dm[i][0]= matrix.get(i).getDoubleValue("f");
			dm[i][1]= matrix.get(i).getDoubleValue("g");
			dm[i][2]= matrix.get(i).getDoubleValue("h");
			dm[i][3]= matrix.get(i).getDoubleValue("c");
			dm[i][4]= matrix.get(i).getDoubleValue("p");
			dm[i][5]= matrix.get(i).getDoubleValue("i");
		}
		double f = dm[0][0]+dm[1][0]+dm[2][0]+dm[3][0]+dm[4][0]+dm[5][0];
		double g = dm[0][1]+dm[1][1]+dm[2][1]+dm[3][1]+dm[4][1]+dm[5][1];
		double h = dm[0][2]+dm[1][2]+dm[2][2]+dm[3][2]+dm[4][2]+dm[5][2];
		double c = dm[0][3]+dm[1][3]+dm[2][3]+dm[3][3]+dm[4][3]+dm[5][3];
		double p = dm[0][4]+dm[1][4]+dm[2][4]+dm[3][4]+dm[4][4]+dm[5][4];
		double i = dm[0][5]+dm[1][5]+dm[2][5]+dm[3][5]+dm[4][5]+dm[5][5];
		for(double[] d:dm) {
			double dd = (d[0]/f+ d[1]/g+ d[2]/h+ d[3]/c+ d[4]/p+ d[5]/i)/6d;
			result.add(dd);
		}
		result.add(consistencyCheck(dm));
		return result;
	}

	public double consistencyCheck(double[][] dm) {
		double[] RI = {0,0,0.58,0.9,1.12,1.24,1.32,1.41,1.45,1.49};
		double[] products = new double[dm.length];
		int index = 0 ;
		for(double[] d:dm) {
			double product = 1d;
			for(double c: d) {
				product = product*c;
			}
			products[index] = product;
			index++;
		}
		double[] root = new double[dm.length];
		int index2 = 0 ;
		for(double c:products) {
			root[index2] = Math.pow(c, 1d/dm.length);
			index2++;
		}
		double sum = 0d;
		for(double c:root) {
			sum = sum+c;
		}
		double[] weight = new double[dm.length];
		int index3 = 0 ;
		for(double c:root) {
			weight[index3] = c/sum;
			index3++;
		}
		double[] aw = new double[dm.length];
		int index5 = 0;
		for(double[] d:dm) {
			double sumaw = 0d;
			int index4 = 0;
			for(double c:d) {
				sumaw = sumaw+c*weight[index4];
				index4++;
			}
			aw[index5]=sumaw;
			index5++;
		}
		double[] awnw = new double[dm.length];
		int index6=0;
		for(double c:aw) {
			awnw[index6]=c/(dm.length*weight[index6]);
			index6++;
		}
		double eigenvalue_max = 0d;
		for(double c:awnw) {
			eigenvalue_max = eigenvalue_max+c;
		}
		double cr = (eigenvalue_max-dm.length)/(dm.length-1d)/RI[dm.length-1];
		return cr;
	}
	
	private static List<Double> MATRIX = null;
	
	private static List<Double> ASSETS_MATRIX = null;
	
	private static List<Double> BUSINESS_MATRIX = null;
	
	private static List<Double> OFFICE__MATRIX = null;

	@Override
	public double getUserWeightScore(JSONObject o) {
		if(MATRIX==null) {
			initMatrix();
		}
		if(N_MATRIX_MAP.containsKey(o.getString("city"))) {
			return getWeightScore(o);
		}
		double[] weight = new double[3];
		weight[0] = getAptitudeWeight(BUSINESS_MATRIX,o);
		weight[1] = getAptitudeWeight(OFFICE__MATRIX,o);
		weight[2] = getAptitudeWeight(ASSETS_MATRIX,o);
		double max = 0d;
		for(double d:weight) {
			if(max<d) {
				max = d;
			}
		}
		return max;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initMatrix();
	}
	
	private double getAptitudeWeight(List<Double> matrix,JSONObject o) {
		double weight = 0;
		if(o.getString("publicFund")!=null&&(o.getString("publicFund").equals("有，个人月缴300-800元")||o.getString("publicFund").equals("有，个人月缴800元以上"))) {
			weight = weight+ matrix.get(0)*MATRIX.get(0);
		}else {
			weight = weight+ 0.005d*MATRIX.get(0);
		}
		if(o.getInteger("getwayIncome")!=null&&o.getInteger("getwayIncome")==1) {
			weight = weight+ matrix.get(1)*MATRIX.get(1);
		}else {
			weight = weight+ 0.005d*MATRIX.get(1);
		}
		if(o.getInteger("house")!=null&&(o.getInteger("house")==1||o.getInteger("house")==2)) {
			weight = weight+ matrix.get(2)*MATRIX.get(2);
		}else {
			weight = weight+ 0.005d*MATRIX.get(2);
		}
		if(o.getInteger("car")!=null&&(o.getInteger("car")==1||o.getInteger("car")==4)) {
			weight = weight+ matrix.get(3)*MATRIX.get(3);
		}else {
			weight = weight+ 0.005d*MATRIX.get(3);
		}
		if(o.getInteger("company")!=null&&o.getInteger("company")==1) {
			weight = weight+ matrix.get(4)*MATRIX.get(4);
		}else {
			weight = weight+ 0.005d*MATRIX.get(4);
		}
		if(o.getInteger("insurance")!=null&&o.getInteger("insurance")==1) {
			weight = weight+ matrix.get(5)*MATRIX.get(5);
		}else {
			weight = weight+ 0.005d*MATRIX.get(5);
		}
		return weight;
	}
	
	private static Map<String,MatrixBo> N_MATRIX_MAP;
	
	private void initMatrix() {
		MATRIX = getMatrix(customerMapper.getMatrix());
		ASSETS_MATRIX = getMatrix(customerMapper.getMatrixAssets());
		BUSINESS_MATRIX = getMatrix(customerMapper.getMatrixBusiness());
		OFFICE__MATRIX = getMatrix(customerMapper.getMatrixOffice());
		N_MATRIX_MAP = getMatrixMap();
		LOG.info("MATRIX = "+JSON.toJSONString(MATRIX)+" , ASSETS_MATRIX=" + JSON.toJSONString(ASSETS_MATRIX) + " ,BUSINESS_MATRIX="+JSON.toJSONString(BUSINESS_MATRIX)+" ,OFFICE__MATRIX="+JSON.toJSONString(OFFICE__MATRIX));
	}

	@Override
	public void replaceMatrix() {
		initMatrix();
	}
	
	private double getWeightScore(JSONObject o) {
		MatrixBo matrixBo = N_MATRIX_MAP.get(o.getString("city"));
		List<Double> weghtList = new ArrayList<Double>();
		List<Double> bMatrix = matrixBo.getBMatrix();
		List<List<Double>> dMatrix = matrixBo.getDMatrix();
		for(List<Double> list:dMatrix) {
			weghtList.add(getAptitudeWeightNew(bMatrix,list,o));
		}
		weghtList.sort(new Comparator<Double>() {

			@Override
			public int compare(Double o1, Double o2) {
				// TODO Auto-generated method stub
				if(o1>o2) {
					return -1;
				}else {
					return 1;
				}
			}
			
		});
		double we= 0d;
		for(int i=0;i<getAptitudeWeight(o);i++) {
			we =we+weghtList.get(i);
		}
		return new BigDecimal(we).divide(BigDecimal.valueOf(3d), 16, RoundingMode.HALF_UP).doubleValue();
	}
	
	private double getAptitudeWeightNew(List<Double> bmatrix,List<Double> dmatrix,JSONObject o) {
		double weight = 0;
		if(o.getString("publicFund")!=null&&(o.getString("publicFund").equals("有，个人月缴300-800元")||o.getString("publicFund").equals("有，个人月缴800元以上"))) {
			weight = weight+ dmatrix.get(0)*bmatrix.get(0);
		}else {
			weight = weight+ 0.005d*bmatrix.get(0);
		}
		if(o.getInteger("getwayIncome")!=null&&o.getInteger("getwayIncome")==1) {
			weight = weight+ dmatrix.get(1)*bmatrix.get(1);
		}else {
			weight = weight+ 0.005d*bmatrix.get(1);
		}
		if(o.getInteger("house")!=null&&(o.getInteger("house")==1||o.getInteger("house")==2)) {
			weight = weight+ dmatrix.get(2)*bmatrix.get(2);
		}else {
			weight = weight+ 0.005d*bmatrix.get(2);
		}
		if(o.getInteger("car")!=null&&(o.getInteger("car")==1||o.getInteger("car")==4)) {
			weight = weight+ dmatrix.get(3)*bmatrix.get(3);
		}else {
			weight = weight+ 0.005d*bmatrix.get(3);
		}
		if(o.getInteger("company")!=null&&o.getInteger("company")==1) {
			weight = weight+ dmatrix.get(4)*bmatrix.get(4);
		}else {
			weight = weight+ 0.005d*bmatrix.get(4);
		}
		if(o.getInteger("insurance")!=null&&o.getInteger("insurance")==1) {
			weight = weight+ dmatrix.get(5)*bmatrix.get(5);
		}else {
			weight = weight+ 0.005d*bmatrix.get(5);
		}
		return weight;
	}
	
	private static int getAptitudeWeight(JSONObject o) {
		int weight = 0;
		if(o.getString("publicFund")!=null&&(o.getString("publicFund").equals("有，个人月缴300-800元")||o.getString("publicFund").equals("有，个人月缴800元以上"))) {
			weight++;
		}
		if(o.getInteger("getwayIncome")!=null&&o.getInteger("getwayIncome")==1) {
			weight++;
		}
		if(o.getInteger("house")!=null&&(o.getInteger("house")==1||o.getInteger("house")==2)) {
			weight++;
		}
		if(o.getInteger("car")!=null&&(o.getInteger("car")==1||o.getInteger("car")==4)) {
			weight++;
		}
		if(o.getInteger("company")!=null&&o.getInteger("company")==1) {
			weight++;
		}
		if(o.getInteger("insurance")!=null&&o.getInteger("insurance")==1) {
			weight++;
		}
		return weight;
	}
	
	private Map<String,MatrixBo> getMatrixMap(){
		List<JSONObject> matrixNew = customerMapper.getMatrixNew();
		Map<String, List<JSONObject>> collect = matrixNew.stream().collect(Collectors.groupingBy(o->o.getString("city")));
		Map<String,MatrixBo> matrixMap = new HashMap<String,MatrixBo>();
		collect.entrySet().forEach(entry->{
			List<JSONObject> value = entry.getValue();
			MatrixBo bo = new MatrixBo();
			List<List<Double>> dMat = new ArrayList<List<Double>>();
			for(JSONObject obj:value) {
				String mString= obj.getString("model");
				List<Double> parseArray = JSON.parseArray(mString, Double.class);
				if(obj.getInteger("type")==0) {
					bo.setBMatrix(parseArray);
				}else {
					dMat.add(parseArray);
				}
			}
			bo.setDMatrix(dMat);
			String[] split = entry.getKey().split(",");
			for(String s:split) {
				matrixMap.put(s, bo);
			}
		});
		return matrixMap;
	}
	
	@Override
	public List<CustomerPO> getCustomer(PageVO<CustomerPO> vo) {
		return customerMapper.getCustomerList(vo);
	}
	
}
