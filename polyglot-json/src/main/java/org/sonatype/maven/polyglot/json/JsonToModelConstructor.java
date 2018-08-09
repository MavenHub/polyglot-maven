package org.sonatype.maven.polyglot.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Extension;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Repository;
import org.apache.maven.model.Resource;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonToModelConstructor {
	
	private JSONObject json;
	private Model model;
		
	public JsonToModelConstructor(JSONObject json) {
		this.json = json;
	}

	public Model construct() {
		try {
		    
		    model = new Model();
		    
		    readHeader();
		    readParent();
		    readProperties();
		    readRepositories();
		    readDependencies();
		    readBuild();
    		readProfiles();
    		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return model;
	}

	private void readHeader() throws IOException {
	    
	    model.setModelVersion(json.getString("modelVersion"));
		model.setGroupId(json.getString("groupId"));
		model.setArtifactId(json.getString("artifactId"));
		model.setPackaging(json.getString("packaging"));
		model.setVersion(json.getString("version"));
	}
	
	private void readParent() throws IOException {
	    
	    JSONObject parentJson = json.optJSONObject("parent");
		if (parentJson != null) {
		    
    		Parent parent = new Parent();
    		model.setParent(parent);
		    
		    parent.setGroupId(parentJson.getString("groupId"));
			parent.setArtifactId(parentJson.getString("artifactId"));
			
			if (!parentJson.optString("version").isEmpty()) {
				parent.setVersion(parentJson.getString("version"));
			}
			if (!parentJson.optString("relativePath").isEmpty()) {
				parent.setRelativePath(parentJson.getString("relativePath"));
			}
		}
	}
	
	private void readProperties() throws IOException {
	    
	    JSONObject propsJson = json.optJSONObject("properties");

		if (propsJson != null && propsJson.length() > 0) {
		    Properties props = new Properties();
		    model.setProperties(props);

			for (Object key : propsJson.names()) {
				props.put(key, propsJson.getString(key.toString()));
			}
		}
	}
	
	private void readRepositories() throws IOException {
		
	    JSONArray reposJson = json.optJSONArray("repositories");

		if (reposJson!= null && reposJson.length() > 0) {
		    
			for (Object repo : reposJson) {
			    
			    JSONObject repoJson = (JSONObject)repo;
			    
			    Repository repository = new Repository();
			    model.getRepositories().add(repository);

				if (!repoJson.optString("url").isEmpty()) {
					repository.setUrl(repoJson.getString("url"));
				}
				if (!repoJson.optString("name").isEmpty()) {
					repository.setName(repoJson.getString("name"));
				}
				if (!repoJson.optString("id").isEmpty()) {
					repository.setId(repoJson.getString("id"));
				}
			}
		}
	}
	
	private void readDependencies() throws IOException {
		JSONObject depMgmtJson = json.optJSONObject("dependencyManagement");
		
		if (depMgmtJson != null) {
    		JSONArray depsJson = depMgmtJson.optJSONArray("dependencies");
    		if(depsJson != null && depsJson.length() > 0 ) {
		      model.getDependencyManagement().setDependencies(readDependencies(depsJson));
    		    
    		}
		}
		
		JSONArray depsJson = json.optJSONArray("dependencies");
		if(depsJson != null && depsJson.length() > 0 ) {
		    model.setDependencies(readDependencies(depsJson));
	    }

	}
	
	private List<Dependency> readDependencies(JSONArray depsJson) throws IOException {
		
		List<Dependency> deps = new ArrayList<>(depsJson.length());
		
		for (Object depObj : depsJson) {
		    
		    JSONObject depJson = (JSONObject) depObj;
		    
		    Dependency dependency = new Dependency();
            deps.add(dependency);
		    
    		dependency.setGroupId(depJson.getString("groupId"));
    		dependency.setArtifactId(depJson.getString("artifactId"));
    		
    		if (depJson.opt("version") != null) {
    			dependency.setVersion(depJson.optString("version"));
    		}
    		if (depJson.opt("scope") != null) {
    			dependency.setScope(depJson.optString("scope"));
    		}
    		if (depJson.opt("optional") != null) {
    			dependency.setOptional(depJson.optString("optional"));
    		}
    		if (depJson.opt("type") != null) {
    			dependency.setType(depJson.optString("type"));
    		}
    		if (depJson.opt("systemPath") != null) {
    			dependency.setSystemPath(depJson.optString("systemPath"));
    		}

			JSONArray exclsJson = depJson.optJSONArray("exclusions");
    		
    		if (exclsJson != null && exclsJson.length() > 0) {
    			
    			for (Object exclObj : exclsJson) {
    			    
    			    JSONObject exclJson = (JSONObject)exclObj;

    				Exclusion exclusion = new Exclusion(); 
    				dependency.addExclusion(exclusion);
    				
    				exclusion.setGroupId(exclJson.getString("groupId"));
    				if (exclJson.opt("artifactId") != null) {
    					exclusion.setArtifactId(exclJson.optString("artifactId"));
    				}
    			}
			}
				
		}
		
		return deps;
	}
	
	
	private void readBuild() throws IOException {
	    
	    JSONObject buildJson = json.optJSONObject("build");
		if (buildJson != null) {
		    
		    Build build = new Build();
		    model.setBuild(build);
		    
			if (buildJson.opt("sourceDirectory") != null) {
				build.setSourceDirectory(buildJson.getString("sourceDirectory"));
			}
			if (buildJson.opt("scriptSourceDirectory") != null) {
				build.setScriptSourceDirectory(buildJson.getString("scriptSourceDirectory"));
			}
			if (buildJson.opt("testSourceDirectory") != null) {
				build.setTestSourceDirectory(buildJson.getString("testSourceDirectory"));
			}
			if (buildJson.opt("outputDirectory") != null) {
				build.setOutputDirectory(buildJson.getString("outputDirectory"));
			}
			if (buildJson.opt("testOutputDirectory") != null) {
				build.setTestOutputDirectory(buildJson.getString("testOutputDirectory"));
			}
			
			JSONArray extsJson = buildJson.optJSONArray("extensions");
    		
			if (extsJson != null && extsJson.length() > 0) {
				
				for (Object extObj : extsJson) {
				    
				    JSONObject extJson = (JSONObject) extObj;
				    Extension extension = new Extension();
				    build.addExtension(extension);
				    
					extension.setGroupId(extJson.getString("groupId"));
					extension.setArtifactId(extJson.getString("artifactId"));
					if (extJson.opt("version") != null) {
						extension.setVersion(extJson.getString("version"));
					}
				}
			}
			
			readBuildBase(buildJson, build);
		}
	}
	
	private void readBuildBase(JSONObject buildJson, BuildBase build) throws IOException {
		
		if (buildJson.opt("defaultGoal") != null) {
			build.setDefaultGoal(buildJson.getString("defaultGoal"));
		}
		if (buildJson.opt("directory") != null) {
			build.setDirectory(buildJson.getString("directory"));
		}
		if (buildJson.opt("finalName") != null) {
			build.setFinalName(buildJson.getString("finalName"));
		}
		
		JSONArray filtersJson = buildJson.optJSONArray("filters");
		if ( filtersJson != null && filtersJson.length() > 0)  {
		    for(Object filter : filtersJson) {
		        build.getFilters().add(filter.toString());
		    }
		}
		
		JSONObject plugMgmtJson = buildJson.optJSONObject("pluginManagement");
		if (plugMgmtJson != null) {
		    build.setPluginManagement(new PluginManagement());
			build.getPluginManagement().setPlugins(readPlugins(plugMgmtJson));
		}
		
		build.setPlugins(readPlugins(buildJson));
		
		build.setResources(readResources(buildJson, "resources"));
		build.setTestResources(readResources(buildJson, "testResources"));
	}
	
	private List<Plugin> readPlugins(JSONObject plugsParentJson) throws IOException {
		
		JSONArray plugsJson = plugsParentJson.optJSONArray("plugins");
		List<Plugin> plugins = new ArrayList<>();
		
		if (plugins != null && !plugins.isEmpty()) {
			
			for (Object plugObj : plugsJson) {
			    
			    JSONObject plugJson = (JSONObject) plugObj;
			    
			    Plugin plugin = new Plugin();
                plugins.add(plugin);
                
        		plugin.setGroupId(plugJson.getString("groupId"));
        		plugin.setArtifactId(plugJson.getString("artifactId"));
        		
        		if (plugJson.opt("version") != null) {
        			plugin.setVersion(plugJson.getString("version"));
        		}
        		if ( plugJson.opt("extensions") != null) {
        			plugin.setExtensions(plugJson.getString("extensions"));
        		}
        		if (plugJson.opt("inherited") != null) {
        			plugin.setInherited(plugJson.getString("inherited"));
        		}
        		
        		JSONArray depsJson = plugJson.optJSONArray("dependencies");
        		if (depsJson != null && depsJson.length() > 0) {
        			plugin.setDependencies(readDependencies(depsJson));
        		}
        		
        		plugin.setConfiguration(plugJson.getString("configuration"));
			}			
		}
		
		return plugins;
	}
	
	
	private List<Resource> readResources(JSONObject rsrcsParentJson, String rsrcsType) throws IOException {
	    
	    List<Resource> resources = new ArrayList<>();
	    JSONArray rsrcsJson = rsrcsParentJson.optJSONArray(rsrcsType);
	    
		if (rsrcsJson != null && rsrcsJson.length() > 0) {
		    
		    Resource resource = new Resource();
		    
			for (Object rsrcObj: rsrcsJson) {
			    
			    JSONObject rsrcJson = (JSONObject) rsrcObj; 
			    resources.add(resource);
			    
        		if (rsrcJson.opt("directory") != null) {			
        			resource.setDirectory(rsrcJson.getString("directory"));	
        		}		
        		if (rsrcJson.opt("filtering") != null) {
        			resource.setFiltering(rsrcJson.getString("filtering"));	
        		}
        		if (rsrcJson.opt("targetPath") != null) {			
        			resource.setTargetPath(rsrcJson.getString("targetPath"));	
        		}
    		    
    		    JSONArray incsJson = rsrcJson.optJSONArray(("includes"));
        		if (incsJson != null && incsJson.length() > 0) {
        		    for(Object inc : incsJson){
        		        resource.getIncludes().add(inc.toString());
        		    }
        		}
        		
    		    JSONArray excsJson = rsrcJson.optJSONArray(("excludes"));
        		if (excsJson != null && excsJson.length() > 0) {
        		    for(Object exc : excsJson){
        		        resource.getExcludes().add(exc.toString());
        		    }
        		}
			}
		}
		
		return resources;
	}
	
	private void readProfiles() throws IOException {
	    
	    JSONArray profsJson = json.optJSONArray("profiles");

		if ( profsJson!= null) {
		    
			for (Object profObj: profsJson) {
			    
			    Profile profile = new Profile();
			    model.getProfiles().add(profile);
			    
			    JSONObject profJson = (JSONObject) profObj;
			    
			    JSONObject actJson = profJson.optJSONObject("activation");
				if (actJson != null) {
				    
				    Activation activation = new Activation();
					profile.setActivation(activation);
					
					if(actJson.optBoolean("activeByDefault")) {
					   activation.setActiveByDefault(actJson.optBoolean("activeByDefault"));
				    }

					if (actJson.opt("jdk") != null) {
						activation.setJdk(actJson.optString("jdk"));
					}
					
				    JSONObject osJson = actJson.optJSONObject("os");
					if (osJson != null) {
						
						ActivationOS actOs = new ActivationOS();
						activation.setOs(actOs);
						
						actOs.setName(osJson.getString("name"));
						actOs.setFamily(osJson.getString("family"));
						actOs.setArch(osJson.getString("arch"));
						actOs.setVersion(osJson.getString("version"));
					}
					
				    JSONObject propJson = actJson.optJSONObject("property");
					if ( propJson != null) {

					    ActivationProperty actProp = new ActivationProperty();
					    activation.setProperty(actProp);
					    
						actProp.setName(propJson.getString("name"));
						actProp.setValue(propJson.getString("value"));
					    
					}
					
				    JSONObject fileJson = actJson.optJSONObject("file");
					if (fileJson != null) {
					    
					    ActivationFile actFile = new  ActivationFile();
					    activation.setFile(actFile);
						
						actFile.setExists(fileJson.getString("exists"));
						actFile.setMissing(fileJson.getString("missing"));
					}
				}
				
    		    JSONObject buildJson = profJson.optJSONObject("build");
				if (buildJson != null) {
				    
				    Build build = new Build();
				    profile.setBuild(build);
					readBuildBase(buildJson, build);
				}				
				
			}
		}
	}
}