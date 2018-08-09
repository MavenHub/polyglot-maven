package org.sonatype.maven.polyglot.json;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Extension;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Repository;
import org.apache.maven.model.Resource;
import org.apache.maven.model.ActivationOS;

public class ModelToJsonWriter {
	
	private JSONObject json;
	private Writer out;
	private Model model;
		
	public ModelToJsonWriter(Writer out, Model model) {
		this.out = out;
		this.model = model;
	}

	public void write() {
		try {

		    json = new JSONObject();
		    
		    writeHeader();
		    writeParent();
		    writeProperties();
		    writeRepositories();
		    writeDependencies();
		    writeBuild();
    		writeProfiles();
    		
            out.write(json.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeHeader() throws IOException {
	    json.put("modelVersion", model.getModelVersion());
		json.put("groupId", model.getGroupId());
		json.put("artifactId", model.getArtifactId());
		json.put("packaging", model.getPackaging());
		json.put("version", model.getVersion());
	}
	
	private void writeParent() throws IOException {
		Parent parent = model.getParent();
		if (parent != null) {
		    
		    JSONObject parentJson = new JSONObject();
		    json.put("parent", parentJson);
		    
		    parentJson.put("groupId", parent.getGroupId());
			parentJson.put("artifactId", parent.getArtifactId());
			
			if (parent.getVersion() != null) {
				parentJson.put("version", parent.getVersion());
			}
			if (parent.getRelativePath() != null) {
				parentJson.put("relativePath", parent.getRelativePath());
			}
		}
	}
	
	private void writeProperties() throws IOException {
		Properties props = model.getProperties();
		if (props != null && !props.isEmpty()) {
		    
		    JSONObject propsJson = new JSONObject();
		    json.put("properties", propsJson);

			for (Object prop : props.keySet()) {
				propsJson.put(prop.toString(), props.get(prop));
			}
		}
	}
	
	private void writeRepositories() throws IOException {
		
		if (model.getRepositories() != null && !model.getRepositories().isEmpty()) {
		    
		    JSONArray reposJson = new JSONArray();
		    json.put("repositories", reposJson);

			for (Repository repository : model.getRepositories()) {
			    
			    JSONObject repoJson = new JSONObject();
			    reposJson.put(repoJson);

				if (repository.getUrl() != null) {
					repoJson.put("url", repository.getUrl());
				}
				if (repository.getName() != null) {
					repoJson.put("name", repository.getName());
				}
				if (repository.getId() != null) {
					repoJson.put("id",  repository.getId());
				}
			}
		}
	}
	
	private void writeDependencies() throws IOException {
		
		if (model.getDependencyManagement() != null && model.getDependencyManagement().getDependencies() != null && !model.getDependencyManagement().getDependencies().isEmpty()) {
			JSONObject depMgmtJson = new JSONObject();
		    json.put("dependencyManagement", depMgmtJson);
		    writeDependencies(model.getDependencyManagement().getDependencies(), depMgmtJson);
		}
		
		writeDependencies(model.getDependencies(), json);
	}
	
	private void writeDependencies(List<Dependency> dependencies, JSONObject depParentJson) throws IOException {
		if (dependencies != null && !dependencies.isEmpty()) {
		    
		    JSONArray depsJson = new JSONArray();
		    depParentJson.put("dependencies", depsJson);
		    
		    
			for (Dependency dependency : dependencies) {
			    
			    JSONObject depJson = new JSONObject();
			    depsJson.put(depJson);
			    
        		depJson.put("groupId", dependency.getGroupId());
        		depJson.put("artifactId", dependency.getArtifactId());
        		
        		if (dependency.getVersion() != null) {
        			depJson.put("version", dependency.getVersion());
        		}
        		if (dependency.getScope() != null) {
        			depJson.put("scope", dependency.getScope());
        		}
        		if (dependency.getOptional() != null) {
        			depJson.put("optional", dependency.getOptional());
        		}
        		if (dependency.getType() != null) {
        			depJson.put("type", dependency.getType());
        		}
        		if (dependency.getSystemPath() != null) {
        			depJson.put("systemPath", dependency.getSystemPath());
        		}
        		
        		if (dependency.getExclusions() != null && !dependency.getExclusions().isEmpty()) {
        			
        			JSONArray exclsJson = new JSONArray();
        			depJson.put("exclusions", exclsJson);
        			
        			for (Exclusion exclusion : dependency.getExclusions()) {
        			    
        			    JSONObject exclJson = new JSONObject();
        			    exclsJson.put(exclsJson);
        				
        				exclJson.put("groupId", exclusion.getGroupId());
        				if (exclusion.getArtifactId() != null) {
        					exclJson.put("artifactId", exclusion.getArtifactId());
        				}
        			}
				}
				
			}
		}			
	}
	
	
	private void writeBuild() throws IOException {
		Build build = model.getBuild();
		if (build != null) {
		    
		    JSONObject buildJson = new JSONObject();
            json.put("build", buildJson);
		    
			if (build.getSourceDirectory() != null) {
				buildJson.put("sourceDirectory", build.getSourceDirectory());
			}
			if (build.getScriptSourceDirectory() != null) {
				buildJson.put("scriptSourceDirectory", build.getScriptSourceDirectory());
			}
			if (build.getTestSourceDirectory() != null) {
				buildJson.put("testSourceDirectory", build.getTestSourceDirectory());
			}
			if (build.getOutputDirectory() != null) {
				buildJson.put("outputDirectory", build.getOutputDirectory());
			}
			if (build.getTestOutputDirectory() != null) {
				buildJson.put("testOutputDirectory", build.getTestOutputDirectory());
			}
			
			List<Extension> extensions = build.getExtensions(); 
			if (extensions != null && !extensions.isEmpty()) {
				
				JSONArray extsJson = new JSONArray();
        		buildJson.put("extensions", extsJson);
        			
				for (Extension extension : extensions) {
				    
				    JSONObject extJson = new JSONObject();
				    extsJson.put(extJson);

					extJson.put("groupId", extension.getGroupId());
					extJson.put("artifactId", extension.getArtifactId());
					if (extension.getVersion() != null) {
						extJson.put("version", extension.getVersion());
					}
				}
			}
			
			writeBuildBase(build, buildJson);
		}
	}
	
	private void writeBuildBase(BuildBase build, JSONObject buildJson) throws IOException {
		
		if (build.getDefaultGoal() != null) {
			buildJson.put("defaultGoal", build.getDefaultGoal());
		}
		if (build.getDirectory() != null) {
			buildJson.put("directory", build.getDirectory());
		}
		if (build.getFinalName() != null) {
			buildJson.put("finalName", build.getFinalName());
		}
		if (build.getFilters() != null && !build.getFilters().isEmpty()) {
		    
		    JSONArray filtersJson = new JSONArray();
            buildJson.put("filters", filtersJson);
            filtersJson.put(build.getFilters());
		    
		}
		
		if (build.getPluginManagement() != null) {
		    
		    JSONObject plugMgmtJson = new JSONObject();
		    buildJson.put("pluginManagement", plugMgmtJson);
			writePlugins(build.getPluginManagement().getPlugins(), plugMgmtJson);
		}
		
		writePlugins(build.getPlugins(), buildJson);
		
		writeResources(build.getResources(), buildJson, "resources");
		writeResources(build.getTestResources(), buildJson, "testResources");
	}
	
	private void writeResources(List<Resource> resources, JSONObject rsrcsParentJson, String rsrcsType) throws IOException {
		if (resources != null && !resources.isEmpty()) {
		    
		    JSONArray rsrcsJson = new JSONArray();
		    rsrcsParentJson.put(rsrcsType, rsrcsJson);
		    
			for (Resource resource : resources) {
			    
			    JSONObject rsrcJson = new JSONObject();
			    rsrcsJson.put(rsrcJson);
			    
        		if (resource.getDirectory() != null) {			
        			rsrcJson.put("directory", resource.getDirectory());	
        		}		
        		if (resource.getFiltering() != null) {			
        			rsrcJson.put("filtering", resource.getFiltering());	
        		}
        		if (resource.getTargetPath() != null) {			
        			rsrcJson.put("targetPath", resource.getTargetPath());	
        		}
        		if (resource.getIncludes() != null && !resource.getIncludes().isEmpty()) {
        		    JSONArray incsJson = new JSONArray();
        		    rsrcJson.put("includes", incsJson);
        		    incsJson.put(resource.getIncludes());	
        		}
        		if (resource.getExcludes() != null && !resource.getExcludes().isEmpty()) {			
        		    JSONArray excsJson = new JSONArray();
        		    rsrcJson.put("excludes", excsJson);
        		    excsJson.put(resource.getExcludes());
        		}

			}	
			
		}
	}
	
	private void writePlugins(List<Plugin> plugins, JSONObject plugsParentJson) throws IOException {
		if (plugins != null && !plugins.isEmpty()) {
			
			JSONArray plugsJson = new JSONArray();
			plugsParentJson.put("plugins", plugsJson);
			
			for (Plugin plugin : plugins) {
			    
			    JSONObject plugJson = new JSONObject();
			    plugsJson.put(plugJson);
			    
        		plugJson.put("groupId", plugin.getGroupId());
        		plugJson.put("artifactId", plugin.getArtifactId());
        		if (plugin.getVersion() != null) {
        			plugJson.put("version", plugin.getVersion());
        		}
        		if (plugin.getExtensions() != null) {
        			plugJson.put("extensions", plugin.getExtensions());
        		}
        		if (plugin.getInherited() != null) {
        			plugJson.put("inherited", plugin.getInherited());
        		}
        		if (plugin.getDependencies() != null && !plugin.getDependencies().isEmpty()) {
        			writeDependencies(plugin.getDependencies(), plugJson);
        		}
        		
        		plugJson.put("configuration", ((Xpp3Dom)plugin.getConfiguration()).toString());
			}			
		}
	}
	
	private void writeProfiles() throws IOException {
		if (model.getProfiles() != null) {
		    
		    JSONArray profsJson = new JSONArray();
		    json.put("profiles", profsJson);
		    
			for (Profile profile : model.getProfiles()) {
			    
			    JSONObject profJson = new JSONObject();
			    profsJson.put(profJson);
			    
				Activation activation = profile.getActivation();
				if (activation != null) {
				    
				    JSONObject actJson = new JSONObject();
				    profJson.put("activation", actJson);
					
					if(activation.isActiveByDefault()) {
					   actJson.put("activeByDefault", activation.isActiveByDefault());
				    }

					if (activation.getJdk() != null) {
						actJson.put("jdk", activation.getJdk());
					}
					
					if (activation.getOs() != null) {
					    JSONObject osJson = new JSONObject();
						actJson.put("os", osJson);
						
						ActivationOS actOs = activation.getOs();
						osJson.put("name", actOs.getName());
						osJson.put("family", actOs.getFamily());
						osJson.put("arch", actOs.getArch());
						osJson.put("version", actOs.getVersion());
					}
					
					if (activation.getProperty() != null) {

					    JSONObject propJson = new JSONObject();
						actJson.put("property", propJson);
					    
					    ActivationProperty actProp = activation.getProperty();
						propJson.put("name", actProp.getName());
						propJson.put("value", actProp.getValue());
					    
					}
					
					if (activation.getFile() != null) {
					    
					    JSONObject fileJson = new JSONObject();
						actJson.put("file", fileJson);
					    
					    ActivationFile actFile = activation.getFile();
						fileJson.put("exists", actFile.getExists());
						fileJson.put("missing", actFile.getMissing());
					}
				}
				
				if (profile.getBuild() != null) {
				    
	    		    JSONObject buildJson = new JSONObject();
                    profJson.put("build", buildJson);

					writeBuildBase(profile.getBuild(), buildJson);
				}				
				
			}
		}
	}
}