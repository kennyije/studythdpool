package com.lyw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lyw.pool.AsyncTask;
import com.lyw.pool.DealOrganization;
import com.lyw.pool.Organization;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudythdpoolApplicationTests {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource(name="anotherMyTaskAsyncPool")
	private ThreadPoolTaskExecutor myTaskAsyncPool;
	@Autowired
	private AsyncTask asyncTask;

	//@Test
	public void AsyncTaskTest() throws InterruptedException, ExecutionException, TimeoutException {
		StringBuilder sb=new StringBuilder();
		long start =System.currentTimeMillis();
		List<Future<String>> results=new ArrayList<>();
		for(int i = 0; i < 100; i++) {
			asyncTask.doTask1(i);
		}
//		for(int i = 0; i < 100; i++) {
//			Future<String> result=asyncTask.doTask2(i);
//			results.add(result);
//		}
//		results.stream().forEach(item->{
//			String str= null;
//			try {
//				str = item.get(1000, TimeUnit.DAYS);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			} catch (ExecutionException e) {
//				e.printStackTrace();
//			} catch (TimeoutException e) {
//				e.printStackTrace();
//			}
//			sb.append(str);
//		});
		ThreadPoolExecutor executor=myTaskAsyncPool.getThreadPoolExecutor();
//		logger.info("线程池名字", );
		executor.shutdown();
		if(myTaskAsyncPool.getThreadPoolExecutor().awaitTermination(6, TimeUnit.SECONDS)){
			long end =System.currentTimeMillis();
			logger.info("耗时{}",end-start);
			executor.shutdownNow();
		}
		logger.info(sb.toString());
		logger.info("All tasks finished.");
	}

//	@Test
	public void contextLoads() {
//		logger.info("{}",TimeUnit.DAYS.toHours(1));
//		logger.info("toMicros{}",TimeUnit.SECONDS.toMicros(1));
//		logger.info("toMillis{}",TimeUnit.SECONDS.toMillis(1));
//		logger.info("toNanos{}",TimeUnit.SECONDS.toNanos(1));
		BigDecimal a = new BigDecimal("0");
		BigDecimal b = new BigDecimal("0.00");


		System.out.println(a.compareTo(b));

	}

	@Test
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
		System.out.println(com.alibaba.fastjson.JSONObject.toJSONString(paList));


	}

	public void deleteEmptyChildren(Object array) {
		if(array instanceof JSONArray){
			JSONArray oa = (JSONArray)array;
			for(int i =0; i< oa.size();i++) {
				deleteEmptyChildren(oa.get(i));
			}
		}else if(array instanceof  JSONObject) {
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
					System.out.println(key+object.toString());
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

	public static String serial(String maxSerial,String prefix){
		if(maxSerial == null || "".equals(maxSerial)){
			return prefix + "0001";
		}
		if(maxSerial.substring(0,8).equals(prefix)){
			return String.valueOf(Long.valueOf(maxSerial) + 1);
		}
		return prefix + "0001";
	}

	@Test
	public void testO() {
		JSONObject object = new JSONObject();
		object.put("serviceRate", "5");
		BigDecimal bbb = object.containsKey("serviceRate")?object.getBigDecimal("serviceRate"):new BigDecimal("0").divide(new BigDecimal("100"));
		System.out.println("结果是"+ bbb);
	}

}
