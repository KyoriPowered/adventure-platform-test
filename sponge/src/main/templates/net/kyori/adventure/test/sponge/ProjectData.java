package net.kyori.adventure.test.sponge;

/* package */ final class ProjectData {
  public static final String ID = "${project.rootProject.name.toLowerCase()}";
  public static final String VERSION = "${project.version}";
  public static final String DESCRIPTION = "${project.description}";

  private ProjectData() {
  }
}
