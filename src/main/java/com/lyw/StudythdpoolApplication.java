package com.lyw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lyw.configuration.TaskThreadPoolConfig;
import com.lyw.pool.DealOrganization;
import com.lyw.pool.Organization;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties({TaskThreadPoolConfig.class})

public class StudythdpoolApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(StudythdpoolApplication.class, args);

	}
	@Override
	public void run(String... strings) throws Exception {
		String aa = getNextMonthDateString("2019-12-31");
		System.out.println(aa);
	}

	/**
	 * 获取任意时间下个月的同一天
	 * 描述:<描述函数实现的功能>.
	 * @param date
	 * @return
	 */
	public  static  String getNextMonthDateString(String date){
		try {
			if(isMonLastDay(date)){
				return getMaxNextMonthDate(date);
			}else{
				SimpleDateFormat dateFormatck = new SimpleDateFormat("yyyy-MM");
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dateFormat.parse(date)); // 设置为当前时间
				calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1); // 设置为上一个月
				//calendar.set(Calendar.DATE, calendar.getMaximum(Calendar.DATE));
				Date dateNew = calendar.getTime();

				if(dateFormatck.format(dateNew).equals(dateFormatck.format(dateFormat.parse(date)))){
					return getMaxNextMonthDate(date);
				}else if(!isMoreOneMon(date,dateFormat.format(dateNew))){
					return getMaxNextMonthDate(date);
				}else{
					return dateFormat.format(dateNew);
				}


			}


		}catch (Exception e) {
			e.printStackTrace();
			return null;

		}
	}
	/**
	 * 获取任意时间下个月的最后一天
	 * 描述:<描述函数实现的功能>.
	 * @param repeatDate
	 * @return
	 */
	private static String getMaxNextMonthDate(String repeatDate) {
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		try {
			if(repeatDate!=null && !"".equals(repeatDate)){
				calendar.setTime(dft.parse(repeatDate));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.add(Calendar.MONTH, +1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return dft.format(calendar.getTime());
	}

	/**
	 *
	 * 功能: 判断是否是俩个日期相差多一个月
	 * @param 日期
	 * @return true 相差一个月,false不是相差一个月
	 */
	public static boolean isMoreOneMon(String str1,String str2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		Calendar bef = Calendar.getInstance();
		Calendar aft = Calendar.getInstance();
		try {
			bef.setTime(sdf.parse(str1));
			aft.setTime(sdf.parse(str2));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
		int month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12;
		//System.out.println(Math.abs(month + result));
		if(1==Math.abs(month + result)){
			return  true;
		}else{
			return  false;
		}

	}

	/**
	 *
	 * 功能: 判断是否是月末
	 * @param 日期
	 * @return true月末,false不是月末
	 */
	public static boolean isMonLastDay(String date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar=Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(date));
			if(calendar.get(Calendar.DATE)==calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				return true;
			}else{
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}


	public void testTree() {
		Organization organization1 = new Organization();
		organization1.setName("众益天成");
		organization1.setId(1L);
		organization1.setPid(0L);

		Organization organization2 = new Organization();
		organization2.setName("风控部");
		organization2.setId(2L);
		organization2.setPid(1L);

		Organization organization3 = new Organization();
		organization3.setName("风控1部");
		organization3.setId(3L);
		organization3.setPid(2L);

		Organization organization4 = new Organization();
		organization4.setName("风控2部");
		organization4.setId(4L);
		organization4.setPid(2L);

		Organization organization5 = new Organization();
		organization5.setName("员工");
		organization5.setId(1l);
		organization5.setPid(4l);
		organization5.setEmp("emp");

		Organization organization6 = new Organization();
		organization6.setName("财务部门");
		organization6.setId(2L);
		organization6.setPid(0L);

		List<Organization> listOfOrganization = new ArrayList<Organization>();
		listOfOrganization.add(organization1);
		listOfOrganization.add(organization2);
		listOfOrganization.add(organization3);
		listOfOrganization.add(organization4);
		listOfOrganization.add(organization5);
		listOfOrganization.add(organization6);

		List<DealOrganization> noPaList = this.getNoParentOrg(listOfOrganization);
		List<DealOrganization> paList = this.getParentOrg(listOfOrganization);

		Map<Long, Long> map = Maps.newHashMapWithExpectedSize(noPaList.size());
		paList.forEach(dealOrganization -> getChild(dealOrganization, noPaList, map));
		JSONArray array = JSONArray.parseArray(JSONObject.toJSONString(paList));
		deleteEmptyChildren(array);



	}

	public void deleteEmptyChildren(Object array) {
		if(array instanceof JSONArray){
			JSONArray oa = (JSONArray)array;
			for(int i =0; i< oa.size();i++) {
				deleteEmptyChildren(oa.get(i));
			}
		}else if(array instanceof JSONObject) {
			JSONObject obj = (JSONObject)array;
			Set<String> keys = obj.keySet();
			Iterator<String> iterator = keys.iterator();
			while (iterator.hasNext()){
				String key = iterator.next();
				if(key.equals("children")&&obj.containsKey("children")&&obj.containsKey("emp")&&"emp".equals(obj.getString("emp"))&&obj.getJSONArray("children").size()==0){
					iterator.remove();
				}

				Object  object = obj.get(key);
				if(object instanceof  JSONArray) {
					JSONArray objArray = (JSONArray) object;
//					if(objArray.size() == 0&&"children".equals(key)) {
//						iterator.remove();
//					}

					deleteEmptyChildren(objArray);

				} else if(object instanceof  JSONObject) {
					deleteEmptyChildren((JSONObject) object);
				} else {
					if(object!=null) {
						System.out.println(key+object.toString());
					}
				}
			}

		}

	}

	/**
	 * 获取顶级目录
	 * @param list
	 * @return
	 */

	public List<DealOrganization> getParentOrg(List<Organization> list) {
		List<DealOrganization> listOfOrganation = new ArrayList<DealOrganization>();
		for(Organization org : list) {
			if(org.getPid().compareTo(0L)==0) {
				DealOrganization dealOrganization = new DealOrganization();
				BeanUtils.copyProperties(org, dealOrganization);
				listOfOrganation.add(dealOrganization);
			}
		}
		return listOfOrganation;
	}

	/**
	 * 获取非顶级目录
	 * @param maxSerial
	 * @param prefix
	 * @return
	 */

	public List<DealOrganization> getNoParentOrg(List<Organization> list) {
		List<DealOrganization> listOfOrganation = new ArrayList<DealOrganization>();
		for(Organization org: list) {
			if(org.getPid().compareTo(0L)!=0){
				DealOrganization dealOrganization = new DealOrganization();
				BeanUtils.copyProperties(org, dealOrganization);
				listOfOrganation.add(dealOrganization);
			}
		}
		return listOfOrganation;
	}

	/**
	 *
	 * @param dealOrganization 上级
	 * @param listOfDealOrgnation 所有数据集合
	 * @param dept 树的深度
	 * @param map
	 */

	public void getChild(DealOrganization dealOrganization, List<DealOrganization> listOfDealOrgnation,  Map<Long,Long> map) {

		List<DealOrganization> childList = Lists.newArrayList();
		listOfDealOrgnation.stream()
				.filter(c -> !map.containsKey(c.getId()))
				.filter(c -> c.getPid().compareTo(dealOrganization.getId())==0)
				.forEach(c -> {
					//放入map,递归循环时可以跳过这个子类，提高循环效率
					map.put(c.getId(), c.getPid());
					//获取当前机构的子机构
					getChild(c, listOfDealOrgnation, map);
					//加入子机构集合
					childList.add(c);
				});
		dealOrganization.setChildren(childList);

	}
}
