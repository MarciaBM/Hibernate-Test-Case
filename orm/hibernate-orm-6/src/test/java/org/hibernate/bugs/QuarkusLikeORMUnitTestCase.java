/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.bugs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.id.SequenceMismatchStrategy;
import org.hibernate.id.enhanced.StandardOptimizerDescriptor;
import org.hibernate.loader.BatchFetchStyle;
import org.hibernate.query.NullPrecedence;

import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
 * <p>
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then
 * submit it as a PR!
 */
@RunWith(BytecodeEnhancerRunner.class) // This runner enables bytecode enhancement for your test.
public class QuarkusLikeORMUnitTestCase extends BaseCoreFunctionalTestCase {

	// Add your entities here.
	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] {
				Car.class,
				Client.class,
				CarsClients.class,
				ClientA.class
//				Foo.class,
//				Bar.class
		};
	}

	// Add in any settings that are specific to your test.  See resources/hibernate.properties for the defaults.
	@Override
	protected void configure(Configuration configuration) {
		super.configure( configuration );

		// For your own convenience to see generated queries:
		configuration.setProperty( AvailableSettings.SHOW_SQL, Boolean.TRUE.toString() );
		configuration.setProperty( AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString() );
		//configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );

		// Other settings that will make your test case run under similar configuration that Quarkus is using by default:
		configuration.setProperty( AvailableSettings.PREFERRED_POOLED_OPTIMIZER, StandardOptimizerDescriptor.POOLED_LO.getExternalName() );
		configuration.setProperty( AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, "10" );
		configuration.setProperty( AvailableSettings.BATCH_FETCH_STYLE, BatchFetchStyle.PADDED.toString() );
		configuration.setProperty( AvailableSettings.QUERY_PLAN_CACHE_MAX_SIZE, "2048" );
		configuration.setProperty( AvailableSettings.DEFAULT_NULL_ORDERING, NullPrecedence.NONE.toString().toLowerCase( Locale.ROOT) );
		configuration.setProperty( AvailableSettings.IN_CLAUSE_PARAMETER_PADDING, "true" );
		configuration.setProperty( AvailableSettings.SEQUENCE_INCREMENT_SIZE_MISMATCH_STRATEGY, SequenceMismatchStrategy.NONE.toString() );

		// Add your own settings that are a part of your quarkus configuration:
		// configuration.setProperty( AvailableSettings.SOME_CONFIGURATION_PROPERTY, "SOME_VALUE" );
	}

	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		// BaseCoreFunctionalTestCase automatically creates the SessionFactory and provides the Session.
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		// Do stuff...


		List<Car> cars = new ArrayList<>();

		for(int i = 0; i < 2; i++) {
			Car car = new Car();
			car.brand = "brand" + i;
			car.model = "model" + i;
//			cars.add(car);
			s.persist(car);
		}

		List<Client> clients = new ArrayList<>();
		for(int i = 0; i < 100; i++) {
			ClientA client = new ClientA();
			client.name = "name" + i;
			client.email = "email" + i;
			client.phone = "phone" + i;
//			clients.add(client);
			s.persist(client);
		}

		s.setJdbcBatchSize(5);
//		s.persist(cars.toArray(new Car[0]));
//		s.persist(clients.toArray(new Client[0]));
		s.flush();

		List<CarsClients> carsClientsList = new ArrayList<>();
		for (Client client : clients) {
			for (Car car : cars) {
				CarsClients carsClients = new CarsClients(car, client, "location");
//				carsClientsList.add(carsClients);
				s.persist(carsClients);
			}
		}

//		s.persist(carsClientsList);
		s.flush();

		tx.commit();
		s.close();
	}
}
