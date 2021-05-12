# graalson
Javax.JSON EE bindings for graaljs

Common pattern to access JSON capabilities directly on graal vm.

        JsonProvider provider = JsonProvider.provider();
        URL test = GraalsonTest.class.getResource("/default.json");
        Reader scriptReader = Files.newBufferedReader(Paths.get(test.toURI()));
        JsonReader reader = Json.createReader(scriptReader);
        JsonObject jsonObject = reader.readObject();
        javax.script.Bindings result = (Bindings) new JsonObjectBindings(jsonObject);
        System.out.println(result.toString());

        Writer writer = new OutputStreamWriter(System.out);
        JsonWriter jwriter = Json.createWriter(writer);
        jwriter.write(jsonObject);

Load from json resource

        {
             "firstName": "John", "lastName": "Smith", "age": 25,
             "address" : {
                 "streetAddress": "21 2nd Street",
                 "city": "New York",
                 "state": "NY",
                 "postalCode": "10021"
             },
             "phoneNumber": [
                 { "type": "home", "number": "212 555-1234" },
                 { "type": "fax", "number": "646 555-4567" }
             ]
        }
  
Or build from objects

         JsonBuilderFactory factory = Json.createBuilderFactory(config);
         JsonObject value = factory.createObjectBuilder()
             .add("firstName", "John")
             .add("lastName", "Smith")
             .add("age", 25)
             .add("address", factory.createObjectBuilder()
                 .add("streetAddress", "21 2nd Street")
                 .add("city", "New York")
                 .add("state", "NY")
                 .add("postalCode", "10021"))
             .add("phoneNumber", factory.createArrayBuilder()
                 .add(factory.createObjectBuilder()
                     .add("type", "home")
                     .add("number", "212 555-1234"))
                 .add(factory.createObjectBuilder()
                     .add("type", "fax")
                     .add("number", "646 555-4567")))
             .build();
             
Add dependency
            
        <dependency>
            <groupId>biz.mofokom</groupId>
            <artifactId>graalson</artifactId>
            <version>1.0.1</version>
        </dependency>
