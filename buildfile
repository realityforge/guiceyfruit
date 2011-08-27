desc "GuiceyFruit Framework"
define 'guiceyfruit' do
  project.version = '2.2-SNAPSHOT'
  project.group = 'org.guiceyfruit'

  compile.options.source = '1.6'
  compile.options.target = '1.6'
  compile.options.lint = 'all'

  define 'core' do

    compile.with :javax_inject,
                 :google_guice,
                 :google_guice_assistedinject,
                 :aopalliance,
                 :javax_ejb,
                 :javax_persistence,
                 :javax_servlet,
        'com.google.guava:guava:jar:r09'

    package :jar
  end
end
