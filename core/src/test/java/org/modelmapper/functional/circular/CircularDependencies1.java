package org.modelmapper.functional.circular;

import static org.testng.Assert.*;

import org.modelmapper.AbstractTest;
import org.modelmapper.Conditions;
import org.modelmapper.ConfigurationException;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * Tests the handling of circular references.
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
@SuppressWarnings("unused")
public class CircularDependencies1 extends AbstractTest {
  private static class A {
    B value = new B();
  }

  private static class B {
    A value;
  }
  
   static class Person {

        @Override
        public String toString() {
            return "Person{" + "name=" + name + ", firstName=" + firstName + ", personRef=" + personRef + '}';
        }
      private String name;
      private String firstName;
      private PersonRef personRef;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getFirstName() {
            return firstName;
        }
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        public PersonRef getPersonRef() {
            return personRef;
        }
        public void setPersonRef(PersonRef personRef) {
            this.personRef = personRef;
        }
    }
  
   static class PersonRef extends Person {
      private String regId="122-AAB";
      
      public String toString(){
          return "Student:: regId:"+getRegId();
      }
        public String getRegId() {
            return regId;
        }
        public void setRegId(String regId) {
            this.regId = regId;
        }
    }

   static class PersonDto {
        
      private String name;
      private String firstName;
      public PersonRef personRef;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getFirstName() {
            return firstName;
        }
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
       
  }

  public void shouldNotThrowOnMatchingCircularReference() {
    B b2 = modelMapper.map(new A(), B.class);
    assertNull(b2.value);
  }
  
  public void skippedCircularRefsAre_OK(){
      ModelMapper myModelMapper = new ModelMapper();
      PersonDto dto = new PersonDto();
      dto.setFirstName("Homer");
      dto.setName("Simpson");
      PropertyMap<PersonDto, Person> propMap =  new PropertyMap<PersonDto, Person>() {
      @Override
      protected void configure() {
        skip().setPersonRef(null);
      }
    };
    myModelMapper.addMappings(propMap);
      Person mappedInstr = myModelMapper.map(dto, Person.class);
      assertEquals(mappedInstr.getFirstName(),dto.getFirstName());
      assertEquals(mappedInstr.getName(),dto.getName());
      assertEquals(mappedInstr.getPersonRef(),dto.personRef);
  }
  
  @Test(expectedExceptions=ConfigurationException.class)
  public void skipped_conditionalCircularRefsAre_NOTSUPPORTED(){
      ModelMapper myModelMapper = new ModelMapper();
      PersonDto dto = new PersonDto();
      dto.setFirstName("Homer");
      dto.setName("Simpson");
      PropertyMap<PersonDto, Person> propMap =  new PropertyMap<PersonDto, Person>() {
      @Override
      protected void configure() {
        when(Conditions.isNull()).skip().setPersonRef(null);
      }
    };
    myModelMapper.addMappings(propMap);
      Person mappedInstr = myModelMapper.map(dto, Person.class);
      assertEquals(mappedInstr.getFirstName(),dto.getFirstName());
      assertEquals(mappedInstr.getName(),dto.getName());
      assertEquals(mappedInstr.getPersonRef(),dto.personRef);
  }
  
  @Test(expectedExceptions=ConfigurationException.class)
  public void not_skippedCircularRefs_ThrowError(){
      ModelMapper myModelMapper = new ModelMapper();
      PersonDto dto = new PersonDto();
      dto.setFirstName("Homer");
      dto.setName("Simpson");
      myModelMapper.map(dto, Person.class);
  }
}
