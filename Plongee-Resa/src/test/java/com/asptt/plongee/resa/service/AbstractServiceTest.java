package com.asptt.plongee.resa.service;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.dao.PlongeeDao;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations={"classpath:/spring/spring-service-impl.xml", "classpath:/spring/spring-dao-inmemory.xml"})
@ContextConfiguration(locations={"classpath:/spring/spring-service-impl.xml",
		"classpath:/spring/spring-dao-jdbc.xml", "classpath:/spring/spring-datasource.xml"})
public abstract class AbstractServiceTest {


	@Autowired
	protected AdherentService adherentService;

	@Autowired
	protected AdherentDao adherentDao;

	@Autowired
	protected PlongeeService plongeeService;

	@Autowired
	protected PlongeeDao plongeeDao;

}
