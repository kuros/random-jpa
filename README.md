# Random-JPA
It has been always been a challenge to create a test data. This project aims at providing easier mechanism to create test data.

## Feature
1. Uses table's foreign key relations to maintain creation order dynamically
2. Creates in memory creation plan which can be used to modify column values before persist.
3. Provides facility to add custom dependency.
4. Provides facility to add random generator (both at class level and attribute level).
5. You can print the creation plan and persisted object hierarchy and easily access the respective objects by index.

## Supported Database
1. Microsoft SQL Server
2. NONE (You can still use functionality, but the dependency has to provided/maintained manually).

## Supported JPA Implementation
1. Hibernate (Version - 4.3.7.Final)

## Usage
In order to use.
### Initialize JPAContextFactory
JPAContextFactory accepts two parameters Database and EntityManager. I am using MS_SQL_SERVER for demo.
```java
JPAContext jpaContext = JPAContextFactory.newInstance(Database.MS_SQL_SERVER, entityManager)
    .create();
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
    .create();
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
                .create();
```
## Using JPAContext
### Creating Plan
Once JPAContext is initialized, you can use it to create plans and persist them accordingly.
Let us say that we want to create two different Employees referring to single Person
```java
Plan plan = Plan.create()
            .add(Entity.of(Employee.class, 2).with(Employee_.Country, INDIA))
            .add(Entity.of(Person.class).with(Person_.gender, "Male");
CreationPlan creationPlan = jpaContext.create(plan);
```
### Modify the creationPlan
Let us say that I want to persist these two employees with different name.
```java
Employee emp1 = creationPlan.get(Employee.class);
emp1.setName("Employee 1");

Employee emp2 = creationPlan.get(Employee.class, 1);
emp2.setName("Employee 2");
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
it will print the hierarchy with the index number of the object followed by
```
└── *ROOT*
    └── com.github.kuros.entity.Person|0
        ├── com.github.kuros.entity.Employee|0
        └── com.github.kuros.entity.Employee|1
```

_Random object creation provided by OpenPojo (http://openpojo.com)_