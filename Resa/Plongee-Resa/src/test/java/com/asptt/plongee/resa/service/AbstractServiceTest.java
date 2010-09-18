package com.asptt.plongee.resa.service;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.asptt.plongee.resa.dao.AdherentDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/spring/spring-service.xml", "classpath:/spring/spring-dao-test.xml", "classpath:/spring/spring-datasource-test.xml"})
public abstract class AbstractServiceTest {


	@Autowired
	protected AdherentService adherentService;

	@Autowired
	protected AdherentDao adherentDao;

}