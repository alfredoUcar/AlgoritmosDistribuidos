Algoritmos Distribuidos
===

Implementación del algoritmo de terminación distribuida de Dijkstra-Scholten mediante hilos y con comunicación entre estos
deberá ser con cola del mensaje que proporciona el beanstalkd.

##Nodo
Un nodo contiene un id y una serie de listas de deudores y varios arrays de sucesores y predecesores. También lleva un registro de sus déficits y el número de nodos entrantes y saliente de el.
En esta clase iniciamos el cliente de beanstalkd

##Grafo
En esta clase se almancena el grafo de la red de nodos. Construye los enlaces que hay en ellos, los predecesores y sucesores de cada uno.

##Enlace
Junta dos nodos en un enlace.

##Mensaje
Se utiliza para que los nodos se envíen mensajes entre ellos, contiene un id y un String que es el mensaje.

##AlgoritmosDistribuidos.java
Este es el main, gestiona el inicio y el tratamiento de resultados del proyecto
---
- Crea los hilos
- Inicia los hilos
- Los finaliza (reaper)
- Guarda los resultados
