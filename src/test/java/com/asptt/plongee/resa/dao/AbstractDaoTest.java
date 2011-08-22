package com.asptt.plongee.resa.dao;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/spring/spring-dao-jdbc.xml", "classpath:/spring/spring-datasource-test.xml"})
public abstract class AbstractDaoTest {


	@Autowired
	protected AdherentDao adherentDao;

	@Autowired
	protected PlongeeDao plongeeDao;

}
