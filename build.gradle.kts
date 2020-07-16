plugins {
    id ("fabric-loom") version "0.4-SNAPSHOT"
}

version = properties["mod_version"] as String + "-mc1.16"
group = properties["maven_group"] as String

base {
    archivesBaseName = properties["archives_base_name"] as String
}

repositories {
    maven {
        url = uri("https://masa.dy.fi/maven")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${properties["yarn_mappings"]}:v2")
    modCompile("net.fabricmc:fabric-loader:${properties["loader_version"]}")

    modCompile("net.fabricmc.fabric-api:fabric-api:${properties["fabric_version"]}")
    
	implementation("com.h2database:h2:1.4.200")
    include("com.h2database:h2:1.4.200")
}

java {
    sourceCompatibility = JavaVersion.VERSION_14
    targetCompatibility = JavaVersion.VERSION_14
}

tasks.getByName<ProcessResources>("processResources"){
    inputs.property("version", project.version)

    from(sourceSets.main.get().resources.srcDirs) {
        include("fabric.mod.json")
        expand("version" to project.version)
    }

    from(sourceSets.main.get().resources.srcDirs) {
        exclude("fabric.mod.json")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("--enable-preview")
}