# graalson
Javax.JSON EE bindings for graaljs


        JsonProvider provider = JsonProvider.provider();
        URL test = GraalsonTest.class.getResource("/rvn.json");
        Reader scriptReader = Files.newBufferedReader(Paths.get(test.toURI()));
        JsonReader reader = Json.createReader(scriptReader);
        JsonObject jsonObject = reader.readObject();
        javax.script.Bindings result = (Bindings) new JsonObjectBindings(jsonObject);
        System.out.println(result.toString());

        Writer writer = new OutputStreamWriter(System.out);
        JsonWriter jwriter = Json.createWriter(writer);
        jwriter.write(jsonObject);
