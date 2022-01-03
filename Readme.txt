Pour lancer le programme:
Sur Eclipse:
-programme arguments dans Run Configurations
-d nom_datafile [required]
-q nom_Query_file [required]
-o nom_Output [required]
-jena [optionnel]
-cache [optionnel] 
-warm prcnt [optionnel: prcnt est le pourcentage warm up]

exemple:

-d 100K.nt
-q STAR_ALL_workload.queryset
-o re
-jena
-cache
-warm 40

Sur Ligne de commande exemple:
pour 100K
java -Xmx2g -jar rdfqengineWindows.jar -d workload2\100K.nt -q workload2\2100Q.queryset -o d100KJvm2g -jena

pour 500K
java -jar rdfqengineWindows.jar -d workload1\500K.nt -q workload1\10MN.queryset -o d500KJvm2g -jena -cache

Sur le Terminale exemple :
java -jar rdfqengineLinux.jar -d workload2/100K.nt -q workload2/2100Q.queryset -o test -jena -cache

=============================================================================
Nos test sont dans target/output où il y a différentes test que nous avons fait