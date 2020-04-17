Copyright 2017-2019, Schlumberger

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
# Introduction 
This project provides client libraries for using OSDU core services.

# Pre-requisites
You need
 
 1. Maven
 2. Java 1.8

# Build and Test
    mvn compile
    mvn test

# Getting started guide

##DpsHeaders
 
This class helps with managing the headers required by the OSDU services e.g.
    
    On-Behalf-Of
    Correlation-Id
    Authorization

It works directly with the client library and can be used to help generate requests to the Data Ecosystem services.  It has options to generate the values yourself or simply take the values fromm an incoming request to forward onto another.
 
##Entitlements client wrapper
Below are integration tests showing usage of the entitlement client wrapper
 
           //setup
           Map<String,String> headers = new HashMap<>();
           headers.put(StandardHeaders.AUTHORIZATION, token);
           headers.put(StandardHeaders.ACCOUNT_ID, "tenant1");
   
           EntitlementsFactory sutFactory = new EntitlementsFactory(EntitlementsConfig.Default());
           IEntitlementsService sut = sutFactory.create(StandardHeaders.createFromMap(headers));
   
   
            //check authorize fails for user as does nto belong to given group
           String groupName = "client-java-int-test" + System.currentTimeMillis();
           try {
               sut.authorizeAny(groupName);
               fail("expected exception");
           }catch (EntitlementsException e){
               assertTrue(e.getMessage().startsWith("User is unauthorized"));
               System.out.println(e);
           }
   
   
           //create a new group
           CreateGroup grp = CreateGroup.builder().name(groupName).description("Integration test group.").build();
           GroupInfo info = sut.createGroup(grp);
           System.out.println("Created group: " + info);
           assertEquals(groupName, info.getName());
           assertEquals("Integration test group.", info.getDescription());
           GroupEmail newGroupEmail = new GroupEmail(info.getEmail());
   
   
           //validate user is now authorized as they are automatically added to group they create
           Groups authorizedGroups = sut.authorizeAny(groupName);
           System.out.println("Authorized groups: " + authorizedGroups);
   
   
           //add a new member to the group
           String groupMember = "new-osdu-memember@osdu-test.com";
           MemberInfo memberInfo = sut.addMember(newGroupEmail, MemberInfo.builder().email(groupMember).role(Roles.MEMBER).build());
           System.out.println("Added member to group: " + memberInfo);
           assertEquals(groupMember, memberInfo.getEmail());
           assertEquals(Roles.MEMBER, memberInfo.getRole());
   
   
           //validate group exists
           Groups groups = sut.getGroups();
           List<GroupInfo> items = groups.getGroups();
           System.out.println("Get groups: " + items);
           assertTrue(items.size() > 0);
           assertEquals("osdu-user@osdutest.com", groups.getMemberEmail());
           assertTrue("Could not find group matching " + info.getEmail(), items.stream().anyMatch( (i) -> i.getEmail().equals(info.getEmail())));
   
   
           //get all members of group
           Members members = sut.getMembers(newGroupEmail, GetMembers.builder().build());
           System.out.println("Got members in group: " + info);
           assertEquals(2, members.getMembers().size());
           assertFalse(members.isMoreToRetrieve());
           assertEquals("osdu-user@osdutest.com", members.getMembers().get(0).getEmail());
           assertEquals(Roles.OWNER, members.getMembers().get(0).getRole());
           assertEquals(groupMember, members.getMembers().get(1).getEmail());
           assertEquals(Roles.MEMBER, members.getMembers().get(1).getRole());
   
   
           //delete member from group
           sut.deleteMember(newGroupEmail.getGroupEmail(), groupMember);
   
   
           //validate group is not returned
           groups = sut.getGroups();
           assertFalse( groups.getGroups().stream().anyMatch( (i) -> i.getEmail() == newGroupEmail.getGroupEmail()));


You can configure the environment and credentials used on Entitlements requests using the config. By default it is configured to work in the P4D environment.

       EntitlementsFactory sutFactory = new EntitlementsFactory(
                EntitlementsAPIConfig
                        .builder()
                        .rootUrl("https://entitlements-com/entitlements/v1") //configure to use entitlements in evt
                        .build());
     
##Legal Client wrapper
    private final static String tenant = "tenant1";

    @Test
    public void should_createLegalTag_deleteLegalTag_retrieveLegalTag_workflow()throws LegalException{
        //setup
        StandardHeaders headers = IntegationTestUtils.getStandardHeaders(tenant);
        LegalFactory sutFactory = new LegalFactory(LegalAPIConfig.Default());
        ILegalService sut = sutFactory.create(StandardHeaders.createFromMap(headers.getHeaders()));

        //create a legaltag
        LegalTag input = new LegalTag();
        Properties props = input.getProperties();
        props.getCountryOfOrigin().add("US");
        props.setContractId("No contract related");
        props.setDataType("EHC Data");
        props.setExportClassification("EAR99");
        props.setOriginator("SLB");
        props.setPersonalData("No personal data");
        props.setExpirationDate("9999-12-31");
        props.setSecurityClassification("Private");
        input.setName(tenant + "-legaltag-clientapi-" + System.currentTimeMillis()); //name should be prefixed with tenant
        input.setProperties(props);
        
        //add legaltag
        LegalTag lt = sut.create(input);
        assertNotNull(lt);
        assertEquals(input, lt);

        //get
        LegalTag lt2 = sut.get(lt.getName());
        assertEquals(lt, lt2);

        //delete
        sut.delete(lt.getName());

        //validate legaltag is deleted
        lt2 = sut.get(lt.getName());
        assertNull(lt2);
    }
 
##Storage service example
    private final static String tenant = "tenant1";
    private final static String ordc = "US";
    private final static String kind = tenant + ":integration:test:1.0.0"; //kind needs to exist if you want it indexing in search - we don't for this test and so use non-existing kind value
    private final static String legaltag = "tenant1-sli-service-test-67try";  //this legaltag needs to actually exist
    private final static String dataGroup = "data.test1@tenant1.osdu-test.com"; //this group needs to actually exist
    private final static String id = "tenant1:clientlib:" + System.currentTimeMillis();

    @Test
    public void should_createRecord_deleteRecord_retrieveRecord_workflow()throws StorageException{
        //setup
        StandardHeaders headers = IntegationTestUtils.getStandardHeaders(tenant);
        StorageFactory sutFactory = new StorageFactory(StorageAPIConfig.Default());
        IStorageService sut = sutFactory.create(StandardHeaders.createFromMap(headers.getHeaders()));

        String kind = tenant + ":integration:test:1.0.0"; //kind needs to exist if you want it indexing in search - we don't for this test and so use non-existing kind value
        Record input = new Record(kind, id);
        input.addLegaltag(legaltag)
            .addOwner(dataGroup)
            .addViewer(dataGroup)
                .addOrdc(ordc);
        input.getData().addProperty("name", "ash"); //assign some data

        //create
        UpsertRecords result = sut.upsertRecord(input);
        assertNotNull(result);
        assertEquals(new Integer(1), result.getRecordCount());
        assertEquals(0, result.getSkippedRecordIds().size());
        assertEquals(1, result.getRecordIds().size());

        //get record
        Record record2 = sut.get(result.getRecordIds().get(0));

        input.setVersion(record2.getVersion());//this is the only difference with the original as version is set by app

        assertEquals(input, record2);

        //delete
        sut.delete(result.getRecordIds().get(0));

        //validate record is deleted
        record2 = sut.get(result.getRecordIds().get(0));
        assertNull(record2);
    }  
    
##Schema example
     private final static String tenant = "tenant1";
     
     @Test
     public void should_createSchema_deleteSchema_retrieveSchema_workflow()throws StorageException{
        //setup
        StandardHeaders headers = IntegationTestUtils.getStandardHeaders(tenant);
        StorageFactory sutFactory = new StorageFactory(StorageAPIConfig.Default());
        IStorageService sut = sutFactory.create(StandardHeaders.createFromMap(headers.getHeaders()));

        String kind = tenant + ":de.core:test:1.0." + System.currentTimeMillis();
        Schema schema = new Schema(kind);
        schema.addStringNode("name")
                .addIntNode("age")
                .addFloatNode("height")
                .addBoolNode("isAdult");

        //create
        Schema result = sut.createSchema(schema);
        assertNotNull(result);
        assertEquals(schema, result);

        //get
        Schema result2 = sut.getSchema(kind);
        assertEquals(schema, result2);

        //delete
        sut.deleteSchema(kind);

        //validate schema is deleted
        result2 = sut.getSchema(kind);
        assertNull(result2);
     }

##Search service example
    private final static String kind = "tenant1:sli-service:sli-record-create-index-search:1.0.0";
    private final static String tenant = "tenant1";

    @Test
    public void should_getResults_for_emptyQueryWithExistingKind()throws Exception {
        //setup
        StandardHeaders headers = IntegationTestUtils.getStandardHeaders(tenant);
        SearchFactory sutFactory = new SearchFactory(SearchAPIConfig.Default());
        ISearchService sut = sutFactory.create(StandardHeaders.createFromMap(headers.getHeaders()));

        //this assumes there is at least 1 record for the given kind
        QueryResult result = sut.getAllKindEntries(kind);
        assertTrue(result.hasResults());
        assertEquals(kind, result.getResults().get(0).get("kind").getAsString()); //verify result matches given kind

        while (result.hasMoreResults()){ //check if we need to get more results from the server

            //make query at next cursor position to get the rest of the results
            Query query = new Query(kind, "");
            query.setCursor(result.getCursor());
            result = sut.query(query);
        }
    }

##Authorize servlet example

    public class MyServlet extends HttpServlet {
    
    	private final EntitlementsFactory sutFactory = new EntitlementsFactory(EntitlementsConfig.Default());
    
    	@Override
    	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
    		StandardHeaders headers = getHeadersInfo(req);
    		if(authorize(headers, resp, "service.myservlet.editor")) {
    			//do get request
    		}
    	}
    
    	private StandardHeaders getHeadersInfo(HttpServletRequest request) {
    
    		Map<String, String> map = new HashMap<String, String>();
    
    		Enumeration headerNames = request.getHeaderNames();
    		while (headerNames.hasMoreElements()) {
    			String key = (String) headerNames.nextElement();
    			String value = request.getHeader(key);
    			map.put(key, value);
    		}
    
    		StandardHeaders headers = StandardHeaders.createFromMap(map);
    		headers.addCorrelationIdIfMissing();
    		return headers;
    	}
    	
    	private boolean authorize(StandardHeaders headers, HttpServletResponse resp, String... roles){
    		IEntitlementsService sut = sutFactory.create(headers);
    		try {
    			sut.authorizeAny(roles);
    			return true;
    		}catch(EntitlementsException e){
    			System.err.println(e);
    			resp.sendError(401, "Unauthorized");
    			return false;
    		}
    	}
    }