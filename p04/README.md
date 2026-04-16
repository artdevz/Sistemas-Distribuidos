# Questionário:

## Q1. O que diferencia uma chamada RPC de uma chamada de função local? Quais são as falhas possíveis que não existem em chamadas locais?

### R1: A local executa no mesmo processo/memória, sendo rápida e previsível, ou seja, falhas são exceções do próprio código, enquanto uma RPC executa em outra máquina/processo, assim, envolvendo rede, serialização e latência. Pode ter falhas como perda de mensagens, timeout ou servidor indisponível. 

## Q2: Explique as semânticas de entrega at-most-once, at-least-once e exactly-once. Em qual delas o gRPC se enquadra por padrão?

### R2: O At-Most-Once a requisição é executada no máximo uma vez, pode não executar, mas jamais duplicará. At-Least-Once, executa uma ou mais vezes, podendo haver duplicação. Exactly-Once, executa exatamente uma vez, é o ideal, porém é mais difícil e caro de garantir. o gRPC segue o At-Most-Once, pois não repete automaticamente chamadas que já chegaram ao servidor, mas pode acontecer duplicações (retries).

## Q3: Por que o Protocol Buffers é mais eficiente que JSON na serialização de mensagens em sistemas distribuídos de alta performance?

### R3: Pois diferente do JSON que é texto, os Protocol Buffers são binários compactados, tendo assim, um parsing mais rápido e tipagem forte.

## Q4: Cite um cenário real em que o uso de streaming bidirecional do gRPC seria mais adequado do que a abordagem request-response convencional.

### R4: Exemplo de bidrecional seria jogos onlines em tempo real, onde clientes enviam ações e o servidor responde com estado atualizado, o que num request-response haveria maior latência -> menos eficiência, além de requisições repetidas.