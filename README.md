# SIM-espaco-tuplas-
 Sistema de Interação Móvel (SIM) usando Espaço de Tuplas do JAVA

### Objetivo
Implementar um Sistema de Interação Móvel (SIM) no qual um usuário utiliza
para entrar em contato com outros usuários que estejam a um determinado raio de alcance do
seu dispositivo.

### Requisitos
- Ao logar no SIM, o usuário deve fornecer um usuário e senha (se não
houver o mesmo deve cadastrar). Conectado, o usuário pode realizar as seguintes ações:
  - Ligar o Radar: ao pressionar o botão para ligar o Radar, o usuário deve ter
fornecido uma distância pela qual o radar irá listar os usuários do sistema
que estão logados no momento. Essa distância deve variar entre 300m e
20km, sendo 1km o valor default. Caso a localização física do cliente mude
mais de 300m, ele deve atualizar sua localização junto ao servidor SIM.
  - Entrar em uma Sala de bate papo e conversar com os usuários que estão
logados na sala.
  - Criar/Apagar uma Sala

- Quando um usuário entra no SIM ele é inscrito em um Espaço de Tuplas, usando um
nick e suas coordenadas físicas.
- Quando o Radar é ligado, o Espaço de Tuplas deve ser consultado para localizar os
Usuários que tenham distâncias compatíveis. 

## Getting started
Projeto construido utilizando JavaFX no Netbeans. Necessário compilar para gerar dist
