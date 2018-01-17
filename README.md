# Random-JPA [![Build Status](https://travis-ci.org/kuros/random-jpa.svg?branch=master)](https://travis-ci.org/kuros/random-jpa) [![Coverage Status](https://coveralls.io/repos/github/kuros/random-jpa/badge.svg?branch=master)](https://coveralls.io/github/kuros/random-jpa?branch=master)  [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.kuros/random-jpa/badge.svg)](http://search.maven.org/#search|ga|1|random-jpa)
It has been always been a challenge to create a test data. This project aims at providing easier mechanism to create test data.

Maven Group Plugin | Latest Version
------------------ | ---------------
com.github.kuros.random-jpa | [v0.5.5](https://github.com/kuros/random-jpa/releases)

## Feature
1. Uses table's foreign key relations to maintain creation order dynamically
2. Creates in memory creation plan which can be used to modify column values before persist.
3. Provides facility to add custom dependency.
4. Provides facility to add random generator (both at class level and attribute level).
5. You can print the creation plan and persisted object hierarchy and easily access the respective objects by index.

## Supported Database
1. Microsoft SQL Server
2. MySQL
3. Oracle
4. Postgres
5. NONE (You can still use functionality, but the dependency has to provided/maintained manually).

## Supported JPA Implementation
1. Hibernate (Version - 4.x, 5.x)
2. EclipseLink

## Usage
In order to use.
### Initialize JPAContextFactory
JPAContextFactory accepts two parameters Database and EntityManager. I am using MS_SQL_SERVER for demo.
```java
JPAContext jpaContext = JPAContextFactory.newInstance(Database.MS_SQL_SERVER, entityManager)
    .generate();
```
#### Initialization with Dependencies
Let us say that you want have a custom dependency between Person and Employee tables but you do not have any foreign key relationship between them.
We will have create Dependencies for this using javax.persistence.metamodel.Attribute objects.
**(Here Person_ & Employee_ are SingularAttributes)**
```java
final Dependencies dependencies = Dependencies.newInstance();
dependencies.withLink(Link.newLink(Person_.id, Employee_.personId))

JPAContext jpaContext = JPAContextFactory.newInstance(Database.MS_SQL_SERVER, entityManager)
    .with(dependencies)
    .generate();
```

#### Creating RandomGenerators
There are two ways in which you can control the random generation behavior.
* At Class level
* At Attribute level.

Let us say that you want all the dates to be current date. And all the Long/Integer values to be positive between 0-1000
```java
final Generator generator = Generator.newInstance();
generator.addClassGenerator(new RandomClassGenerator() {
            @Override
            public Collection<Class<?>> getTypes() {
                final List<Class<?>> classes = Lists.newArrayList();
                classes.add(Date.class);
                return classes;
            }

            @Override
            public Object doGenerate(final Class<?> aClass) {
                return new Date();
            }
        });

generator.addClassGenerator(new RandomClassGenerator() {
            @Override
            public Collection<Class<?>> getTypes() {
                final List<Class<?>> classes = Lists.newArrayList();
                classes.add(Long.class);
                classes.add(Integer.class);
                return classes;
            }

            @Override
            public Object doGenerate(final Class<?> aClass) {
                return org.apache.commons.lang.RandomUtils.nextInt(1000);
            }
        });
```
You can also override random generation for specific attributes, to do that use RandomAttributeGenerator.
Let us say that you want all the employee name to start with "Test-" followed by random string.
```java

generator.addAttributeGenerator(new RandomAttributeGenerator() {
            @Override
            public List<? extends Attribute> getAttributes() {
                final List<SingularAttribute> singularAttributes = new ArrayList<SingularAttribute>();

                singularAttributes.add(Employee_.state);
                return singularAttributes;
            }

            @Override
            public Object doGenerate() {
                return "Test-" + RandomStringUtils.randomAlphanumeric(2);
            }
        });

```

#### Initializing RandomGenerators
```java
final JPAContext jpaContext = JPAContextFactory.newInstance(Database.MS_SQL_SERVER, entityManager)
                .with(generator)
                .generate();
```

### Adding Preconditions
There are scenario's where we want to create some schema in advance before creating our table, and this schema doesn't fall under the hierarchy of main table.
To handle such scenarios, we can define **PreConditions**
```java

final JPAContext jpaContext = JPAContextFactory.newInstance(Database.MS_SQL_SERVER, entityManager)
                .withPreconditions(Before.of(Employee.class)
                                            .create(Entity.of(Department.class), Entity.of(XXX.class)),
                                   Before.of(Person.class)
                                            .create(Entity.of(Y.class), Entity.of(Z.class)))
                .generate();

```

## Using JPAContext
### Creating Plan
Once JPAContext is initialized, you can use it to create plans and persist them accordingly.
Let us say that we want to create two different Employees referring to single Person
Or
```java
CreationPlan creationPlan = jpaContext.create(
            Entity.of(Employee.class, 2).with(Employee_.Country, INDIA),
            Entity.of(Person.class).with(Person_.gender, "Male"));
```
or
```java
Plan plan = Plan.create()
            .add(Entity.of(Employee.class, 2).with(Employee_.Country, INDIA))
            .add(Entity.of(Person.class).with(Person_.gender, "Male");
CreationPlan creationPlan = jpaContext.create(plan);
```
or
```java
Plan plan = Plan.of(
            Entity.of(Employee.class, 2).with(Employee_.Country, INDIA),
            Entity.of(Person.class).with(Person_.gender, "Male"));

CreationPlan creationPlan = jpaContext.create(plan);
```

### Modify the creationPlan
Let us say that I want to persist these two employees with different name.
```java
creationPlan.set(Employee_.name, "Employee 1");

creationPlan.set(1, Employee_.name, "Employee 2");
```

### Printing the creationPlan
You need to provide a printer, which will print the string.
```java
creationPlan.print(new Printer() {
            @Override
            public void print(final String string) {
                System.out.println(string); // you can use logger
            }
        });
```

it will print the hierarchy with the index number of the object followed by
```
└── *ROOT*
    └── com.github.kuros.entity.Person|0
        ├── com.github.kuros.entity.Employee|0
        └── com.github.kuros.entity.Employee|1
```

### Persisting the creationPlan
```java
final ResultMap resultMap = jpaContext.persist(creationPlan);
```

### Fetching the persisted objects
```java
Employee emp1 = resultMap.get(Employee.class);
System.out.println(emp1.getName()); // Prints - Employee 1

Employee emp2 = resultMap.get(Employee.class, 1);
System.out.println(emp2.getName()); //Prints - Employee 2
```
### Printing the Persisted objects
You need to provide a printer, which will print the string.
```java
resultMap.print(new Printer() {
            @Override
            public void print(final String string) {
                System.out.println(string); // you can use logger
            }
        });
```
it will print the hierarchy with the index number of the object followed by the primary key of the persisted objects
```
└── *ROOT*
    └── com.github.kuros.entity.Person|0 [personId: 1]
        ├── com.github.kuros.entity.Employee|0 [employeeId: 1]
        └── com.github.kuros.entity.Employee|1 [employeeId: 2]
```
