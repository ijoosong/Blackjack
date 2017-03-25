package com.YoungSong;

import java.util.*;

/**
 * Created by Young on 3/20/2017.
 */
public class PlayGame {
    Scanner scanner = new Scanner(System.in);
    private static LinkedList<String> cardSet = new LinkedList<>();
    private static Map<Integer, Player> players = new HashMap<>();
    private int sumOfDealerCards ;
    private int maxPlayerNum;
    private int numOfDeck;
    public int neededAmount;
    public String msg1 = "You need to buy more chips at least $ ";
    public String msg2 = "Enter amount to buy chips ... $ ";

    public static boolean isAceEleven;
    public static boolean isAnyOneAlive ;   //  to find out anyone alive for dealing dealer's card

    Player dealer = new Player("Dealer", 1000000);

    public PlayGame(int maxPlayerNum, int numOfDeck) {
        this.maxPlayerNum = maxPlayerNum;
        this.numOfDeck = numOfDeck;
        shuffleCards(numOfDeck);
//        players.put(1,new Player("Young",10000));
        players.put(5,new Player("Randy",10000));
        players.put(2,new Player("Annie",10000));
        players.put(4,new Player("Cassidy",10000));
//        players.put(6,new Player("Clara",10000));
         players.put(3,new Player("Joseph",10000));
    }

    public void startGame() {
        if(cardSet.size() < numOfDeck*52/5){                       //  check remaining cards for suffling again.
            shuffleCards(this.numOfDeck);
        }

        if(players.size() > 0 && bettingToStart()){      //  players can bet now to start
            showCards(30);                          //  show first 30 cards to confirm but do not need later.
            //  draw 1st card for all
            for(Integer i : players.keySet()){
                Player player = players.get(i);
                if(player.getInicialBet() > 0) {
                    drawCard(player, 11);
                    player.setFirstcard(player.onHand.get(11));
                }
            }
            drawCard(dealer,11);
            dealer.setFirstcard(dealer.onHand.get(11));
            //  draw 2nd card for all
            for(Integer i : players.keySet()){
                Player player = players.get(i);
                if(player.getInicialBet() > 0) {
                    drawCard(player, 12);
                }
            }
            drawCard(dealer,12);
            //  show 1st and 2nd card which are given to players
            for(Integer i : players.keySet()) {
                if (players.get(i).getInicialBet() > 0) {
                    System.out.println(players.get(i).getName() + "(#" + i + ")    " + players.get(i).getFirstcard() +
                                        ", " + players.get(i).onHand.get(12));
                }
            }

            isAceEleven = false;                //  it will control Ace card to be 1 or 11
            isAnyOneAlive = false;              //  starting with 'false' but will change to 'true' when anyone stay on

            if(isBlackJack(dealer)){
                //  When dealer has a blackjack, do this
                System.out.println("Dealer : " + dealer.getFirstcard() + ", " + dealer.onHand.get(12));
                for(Integer i : players.keySet()){
                    Player player = players.get(i);
                    if(player.getInicialBet() > 0) {
                        if (isBlackJack(player)) {
                            player.setPush(player.getInicialBet());
                        } else {
                            player.setLoose();
                        }
                    }
                }
            } else {
                //  if dealer doesn't have a blackjack, do this
                System.out.println("Dealer : ?, " + dealer.onHand.get(12));
                for(Integer i : players.keySet()) {
                    Player player = players.get(i);
                    if(player.getInicialBet() > 0) {        //  check who did betting or not
                        if (isBlackJack(player)) {          //  if player has a blackjack, process 'win' and clear
                            System.out.println("You got a Black Jack. Let me pay it.");
                            player.setBlackjack(player.getInicialBet());
                            player.setFirstBetAmount(0);
                            player.setInicialBet(0);
                            player.setFirstcard("");
                            player.onHand.clear();
                        } else {
                            enjoyYourGame(player);          //  no blackjack for dealer and players. regular dealing
                        }
                    }
                }
                sumOfDealerCards = 0;
                if(isAnyOneAlive) {                         //  if no one stay alive, sumOfDealerCards will be 0
                    sumOfDealerCards = dealingDealerCard();
                } else {
                    System.out.println("All players had bad luck.");
                }
                calculateAllHands(sumOfDealerCards);        //  clean the table after pay or take the chips
            }
            //  display game result.     may not need this later but it needs now to check the process
            for(Integer x : players.keySet()) {
                System.out.println(players.get(x).getScore());
            }
            initializeAll();                                //  initialize data before start a new game
        } else {
            System.out.println("No player. Add a player first.");
        }
    }

    private boolean bettingToStart(){
        boolean isAnyoneBet = false;
        for (Integer i : players.keySet()){
            Player player = players.get(i);
            int betAmount = 2;                              //  bet $2 automatically to keep running.
//            int betAmount = getAmount("Spot #" + i + " has $ " + player.getCurrentAmount() +
//                                    "\nHow much will you bet? ... $ ");           //  get amount to bet
            if(betAmount > 0 && betAmount <= player.getCurrentAmount()){
                player.setCurrentAmount(-betAmount);
                player.setInicialBet(betAmount);
                player.setFirstBetAmount(betAmount);
                isAnyoneBet = true;
            }
        }
        return isAnyoneBet;
    }

    private void enjoyYourGame(Player player){              //  Now, dealer has no blackjack.  Regular game.
        String firstCard = player.getFirstcard();
        String dealer2ndCard = dealer.onHand.get(12);
        boolean isSplitCondition = false;

        //  On player's 1st hand, save the condition to split. The condition is depend on the dealer's open card.
        if(dealer2ndCard.equals("A") || dealer2ndCard.equals("10") || dealer2ndCard.equals("9") || dealer2ndCard.equals("8")){
            isSplitCondition = firstCard.equals("9") || firstCard.equals("8");
        } else if(dealer2ndCard.equals("7")){
            isSplitCondition = firstCard.equals("8") || firstCard.equals("7");
        } else {
            isSplitCondition = !firstCard.equals("A") && Integer.parseInt(firstCard) < 9;
        }

        if(!firstCard.equals(player.onHand.get(12))) {
            startDealing(player, 1);                                 //  two cards are not same.
        } else if (firstCard.equals("A")){
            splitWithAcer(player);                                            //  Player has a pair of Ace.
        } else if(isSplitCondition) {
            splitWithOthers(player);                                          //  Positive condition to split.
        } else {
            startDealing(player,1);                                  //  Negative condition to split.
        }
    }

    private void splitWithAcer(Player player){
        neededAmount = player.getInicialBet() - player.getCurrentAmount();    //  To confirm player has enough chips
        if(!hasEnoughChip(player, neededAmount, msg1, msg2)){
            startDealing(player, 1);                                 //  Player didn't buy enough chips
        } else {                                            //  1st split with two 'A's
            isAnyOneAlive = true;                           //  to make dealer take card.
            drawCard(player, 12);
            player.onHand.put(21, "A");
            drawCard(player, 22);
            player.setCurrentAmount(-player.getInicialBet());        //  reduce amount from player's hand to split
            player.setSecondBetAmount(player.getInicialBet());
        }
    }

    private void splitWithOthers(Player player){
        String firstCard = player.getFirstcard();
        neededAmount = player.getInicialBet() - player.getCurrentAmount();    //  confirm player has enough chips
        if(!hasEnoughChip(player, neededAmount, msg1, msg2)){
            startDealing(player,1);                                  //  Player didn't buy enough chips
        } else {                                                     //  1st split happen with 1st hand.
            player.setSecondBetAmount(player.getInicialBet());
            player.setCurrentAmount(-player.getInicialBet());
            player.onHand.put(21, firstCard);
            drawCard(player, 12);
            if (firstCard.equals(player.onHand.get(12))) {           //  after 1st split, again 1st hand got a pair.
                neededAmount = player.getInicialBet() - player.getCurrentAmount();
                if (!hasEnoughChip(player, neededAmount, msg1, msg2)) {
                    startDealing(player, 1);                //  not enough chips to split, let's play 1st hand.
                    drawCard(player, 22);                        //  draw 2nd card for 2nd hand.
                    if (firstCard.equals(player.onHand.get(22))) {   //  2nd hand got a pair after 1st hand done.
                        neededAmount = player.getInicialBet() - player.getCurrentAmount();
                        if (!hasEnoughChip(player, neededAmount, msg1, msg2)) {
                            startDealing(player, 2);        //  Player didn't split. Let's play on 2nd hand.
                        } else {                                     //  2nd split with 2nd hand, so player has 3 hands.
                            drawCard(player, 22);
                            startDealing(player, 2);

                            player.setCurrentAmount(-player.getInicialBet());
                            player.setThirdBetAmount(player.getInicialBet());
                            player.onHand.put(31, firstCard);
                            drawCard(player, 32);
                            startDealing(player, 3);
                        }
                    } else {
                        startDealing(player, 2);            //  2nd hand didn't get a pair, so let's play.
                    }
                } else {                                             //  2nd split with 1st hand, so player has 3 hands.
                    drawCard(player, 12);
                    startDealing(player, 1);

                    drawCard(player, 22);
                    startDealing(player, 2);

                    player.setCurrentAmount(-player.getInicialBet());
                    player.setThirdBetAmount(player.getInicialBet());
                    player.onHand.put(31, firstCard);
                    drawCard(player, 32);
                    startDealing(player, 3);
                }
            } else {
                startDealing(player,1);                     //  after 1st split, 1st hand didn't get a pair.
                drawCard(player,22);
                if (firstCard.equals(player.onHand.get(22))) {       //  2nd hand got a pair after 1st hand done.
                    neededAmount = player.getInicialBet() - player.getCurrentAmount();
                    if (!hasEnoughChip(player, neededAmount, msg1, msg2)) {
                        startDealing(player, 2);            //  Player didn't split. Let's play on 2nd hand.
                    } else {                                //  2nd split with 2nd hand, so let's play 2nd and 3rd hand.
                        drawCard(player, 22);
                        startDealing(player, 2);

                        player.setCurrentAmount(-player.getInicialBet());
                        player.setThirdBetAmount(player.getInicialBet());
                        player.onHand.put(31, firstCard);
                        drawCard(player, 32);
                        startDealing(player, 3);
                    }
                } else {
                    startDealing(player, 2);                //  2nd hand didn't get a pair, so let's play.
                }
            }
        }
    }

    private void calculateAllHands(int sumOfDealerCards){
        System.out.println();
        //  show all player's cards and dealer's cards too
        for(Integer spotNo : players.keySet() ){
            Player player = players.get(spotNo);
            if(player.getFirstBetAmount()>0){
                showFinal(player,11);
            }
            if(player.getSecondBetAmount()>0){
                showFinal(player,21);
            }
            if(player.getThirdBetAmount()>0){
                showFinal(player,31);
            }
        }
        System.out.print("Dealler: ");
        for(Integer i : dealer.onHand.keySet()){
            System.out.print(dealer.onHand.get(i) + ", ");
        }
        //  compare player's cards & dealer's and decide to pay or take.
        for(Integer spotNo : players.keySet() ){
            Player player = players.get(spotNo);
            if(sumOfDealerCards == 0){                  //  All player's hands got over 21.  It means all players lost.
                if(player.getFirstBetAmount()>0){
                    player.setLoose();
                }
                if(player.getSecondBetAmount()>0){
                    player.setLoose();
                }
                if(player.getThirdBetAmount()>0){
                    player.setLoose();
                }
            } else if(sumOfDealerCards > 21) {          //  Dealer got over 21.  So players won except who had over 21.
                if (player.getFirstBetAmount() > 0) {
                    if(makeSumOfCards(player,1) > 21) {             //  Player didn't make double down on 1st hand.
                        player.setLoose();
                    } else {
                        player.setWin(player.getFirstBetAmount());
                        if(player.getInicialBet() < player.getFirstBetAmount()){
                            player.setWin(0);                           //  Player made double down on 1st hand.
                        }
                    }
                }
                if (player.getSecondBetAmount() > 0) {
                    if(makeSumOfCards(player,2) > 21) {             //  Player didn't make double down on 2nd hand.
                        player.setLoose();
                    } else {
                        player.setWin(player.getSecondBetAmount());
                        if(player.getInicialBet() < player.getSecondBetAmount()){
                            player.setWin(0);                           //  Player made double down on 2nd hand.
                        }
                    }
                }
                if (player.getThirdBetAmount() > 0) {
                    if(makeSumOfCards(player,3) > 21) {             //  Player didn't make double down on 3rd hand.
                        player.setLoose();
                    } else {
                        player.setWin(player.getThirdBetAmount());
                        if(player.getInicialBet() < player.getThirdBetAmount()){
                            player.setWin(0);                           //  Player made double down on 3rd hand.
                        }
                    }
                }
            } else {                                    //  Let's compare to figure who has winning hand.
                if (player.getFirstBetAmount() > 0) {
                    int sumOfPlayerHand = makeSumOfCards(player,1);
                    if(sumOfPlayerHand > 21) {
                        player.setLoose();
                    } else if(sumOfPlayerHand < sumOfDealerCards){
                        player.setLoose();
                        if (player.getInicialBet() < player.getFirstBetAmount()) {
                            player.setLoose();          //  Player made double on this hand, so loose double
                        }
                    } else if(sumOfPlayerHand > sumOfDealerCards) {
                        player.setWin(player.getFirstBetAmount());
                        if(player.getInicialBet() < player.getFirstBetAmount()){
                            player.setWin(0);           //  Player made double on this hand, so win double
                        }
                    } else {
                        player.setPush(player.getFirstBetAmount());
                    }
                }
                if (player.getSecondBetAmount() > 0) {
                    int sumOfPlayerHand = makeSumOfCards(player,2);
                    if(sumOfPlayerHand > 21){
                        player.setLoose();
                    } else if(sumOfPlayerHand < sumOfDealerCards) {
                        player.setLoose();
                        if(player.getInicialBet() < player.getSecondBetAmount()){
                            player.setLoose();
                        }
                    } else if(sumOfPlayerHand > sumOfDealerCards) {
                        player.setWin(player.getSecondBetAmount());
                        if(player.getInicialBet() < player.getSecondBetAmount()){
                            player.setWin(0);
                        }
                    } else {
                        player.setPush(player.getSecondBetAmount());
                    }
                }
                if (player.getThirdBetAmount() > 0) {
                    int sumOfPlayerHand = makeSumOfCards(player, 3);
                    if (sumOfPlayerHand > 21){
                        player.setLoose();
                    } else if(sumOfPlayerHand < sumOfDealerCards) {
                        player.setLoose();
                        if(player.getInicialBet() < player.getThirdBetAmount()){
                            player.setLoose();
                        }
                    } else if(sumOfPlayerHand > sumOfDealerCards) {
                        player.setWin(player.getThirdBetAmount());
                        if(player.getInicialBet() < player.getThirdBetAmount()){
                            player.setWin(0);
                        }
                    } else {
                        player.setPush(player.getThirdBetAmount());
                    }
                }
            }
        }
    }

    private void showFinal(Player player, int key){
        System.out.print(player.getName() + " :   ");
        for(int i=key; i<key+9; i++) {
            if (player.onHand.containsKey(i)) {
                System.out.print(player.onHand.get(i) + ", ");
            }
        }
        System.out.println();
    }

    private void startDealing(Player player, int whichHand){
        if(dealer.onHand.get(12).equals("A") || Integer.parseInt(dealer.onHand.get(12)) > 7){
            dealingWithBig(player, whichHand);
        } else if(dealer.onHand.get(12).equals("7")){
            dealingWith7(player, whichHand);
        } else if (dealer.onHand.get(12).equals("2")) {
            dealingWith2(player, whichHand);
        } else {
            dealingWithSmall(player,whichHand);
        }
    }

    private boolean doubleDown(Player player, int whichHand){
        neededAmount = player.getInicialBet() - player.getCurrentAmount();
        switch (whichHand){
            case 1:
                if(hasEnoughChip(player, neededAmount, msg1, msg2)) {
                    player.setCurrentAmount(-player.getInicialBet());
                    player.setFirstBetAmount(player.getInicialBet()*2);
                    drawCard(player, whichHand*10+3);
                    return isAnyOneAlive = true;
                }
                break;
            case 2:
                if(hasEnoughChip(player, neededAmount, msg1, msg2)) {
                    player.setCurrentAmount(-player.getInicialBet());
                    player.setSecondBetAmount(player.getInicialBet()*2);
                    drawCard(player, whichHand*10+3);
                    return isAnyOneAlive = true;
                }
                break;
            case 3:
                if(hasEnoughChip(player, neededAmount, msg1, msg2)) {
                    player.setCurrentAmount(-player.getInicialBet());
                    player.setThirdBetAmount(player.getInicialBet()*2);
                    drawCard(player, whichHand*10+3);
                    return isAnyOneAlive = true;
                }
                break;
        }
        return false;
    }

    private void dealingWithBig(Player player, int whichHand){      //  When dealer's open card is "8, 9, 10, or A"
        int key = whichHand * 10 + 3;
        int sumOfCards = makeSumOfCards(player, whichHand);
        boolean doWhile = true;
        if(sumOfCards==11 || (sumOfCards==10 && (dealer.onHand.get(12).equals("9") || dealer.onHand.get(12).equals("8") ))){
            doWhile = !doubleDown(player,whichHand);
        }
        while(doWhile) {
            if(sumOfCards > 21){
                return;
            } else if(sumOfCards >= 19) {
                isAnyOneAlive = true;
                return;
            } else if(sumOfCards >= 17 ) {
                if (!isAceEleven) {
                    isAnyOneAlive = true;
                    return;
                }
            }
            drawCard(player,key);
            sumOfCards = makeSumOfCards(player, whichHand);
            if(++key > whichHand*10+9){
                return;
            }
        }
    }

    private void dealingWith7(Player player, int whichHand){
        int key = whichHand * 10 + 3;
        int sumOfCards = makeSumOfCards(player, whichHand);
        boolean doWhile = true;
        if(sumOfCards==11 || sumOfCards==10){
            doWhile = !doubleDown(player,whichHand);
        }
        while(doWhile) {
            if(sumOfCards > 21){
                return;
            } else if (sumOfCards >= 18) {
                isAnyOneAlive = true;
                return;
            } else if (sumOfCards >= 16) {
                if (!isAceEleven) {
                    isAnyOneAlive = true;
                    return;
                }
            }
            drawCard(player, key);
            sumOfCards = makeSumOfCards(player, whichHand);
            if (++key > whichHand * 10 + 9) {
                return;
            }
        }
    }

    private void dealingWith2(Player player, int whichHand){
        int key = whichHand * 10 + 3;
        int sumOfCards = makeSumOfCards(player, whichHand);
        boolean doWhile = true;
        if((isAceEleven && sumOfCards <= 18) || sumOfCards==11 || sumOfCards==10) {
            doWhile = !doubleDown(player,whichHand);
        }
        while(doWhile) {
            if (sumOfCards > 11) {
                isAnyOneAlive = true;
                return;
            }
            drawCard(player, key);
            sumOfCards = makeSumOfCards(player, whichHand);
            if (++key > whichHand * 10 + 9) {
                return;
            }
        }
    }

    private void dealingWithSmall(Player player, int whichHand){      //  when dealer's card is 3, 4, 5, or 6.
        int key = whichHand * 10 + 3;
        int sumOfCards = makeSumOfCards(player, whichHand);
        boolean doWhile = true;
        if((isAceEleven && sumOfCards <= 18) || sumOfCards==11 || sumOfCards==10 || sumOfCards==9) {
            doWhile = !doubleDown(player,whichHand);
        }
        while(doWhile) {
            if (sumOfCards > 11) {
                isAnyOneAlive = true;
                return;
            }
            drawCard(player, key);
            sumOfCards = makeSumOfCards(player, whichHand);
            if (++key > whichHand * 10 + 9) {
                return;
            }
        }
    }

    private int dealingDealerCard(){
        int key = 13;
        while(true) {
            int sumOfCards = makeSumOfCards(dealer, 1);
            if(sumOfCards > 16) {
                return sumOfCards;
            } else {
                drawCard(dealer, key);
            }
            if(++key > 19){
                return sumOfCards;
            }
        }
    }

    private int makeSumOfCards(Player player, int key){
        int sum = 0;
        key = key*10+1;
        isAceEleven = false;
        for(int i=key; i<key+9; i++){
            if(player.onHand.containsKey(i)) {
                if (player.onHand.get(i).equals("A")) {
                    if (sum > 10) {
                        sum += 1;
                    } else {
                        sum += 11;
                        isAceEleven = true;
                    }
                } else {
                    sum += Integer.parseInt(player.onHand.get(i));
                    if (sum > 21){
                        if(isAceEleven) {
                            sum -= 10;
                            isAceEleven = false;
                        }
                    }
                }
            } else {
                return sum;
            }
        }
        return sum;
    }

    private boolean isBlackJack(Player player){
        if(player.getFirstcard().equals("A")){
            if(player.onHand.get(12).equals("10")){
                return true;
            }
        } else if(player.getFirstcard().equals("10")){
            if(player.onHand.get(12).equals("A")){
                return true;
            }
        }
        return false;
    }

    private void initializeAll(){
        for(Integer i : players.keySet()) {
            Player player = players.get(i);
            player.setFirstBetAmount(0);
            player.setSecondBetAmount(0);
            player.setThirdBetAmount(0);
            player.setFirstcard("");
            player.setInicialBet(0);
            player.onHand.clear();
        }
        dealer.onHand.clear();
    }

    private void showCards(int count){
        System.out.println(cardSet.size());
        for(int i=0; i<count; i++){
            try {
                System.out.print(cardSet.get(i) + ", ");
                if ((i + 1) % 8 == 0) {
                    System.out.println();
                }
            } catch (NullPointerException e){
                System.out.println();
                i = count;
            }
        }
        System.out.println();
    }

    private boolean hasEnoughChip(Player player, int neededAmount, String msg1, String msg2){
        if(neededAmount > 0) {
            return (buyChips(player, msg1 + neededAmount + "\n" + msg2) >= neededAmount);
        }
        return true;
    }

    private int buyChips(Player player, String message){
        int amount = getAmount(message);
        if (amount > 0) {
            player.setPocketAmount(amount);
            player.setCurrentAmount(amount);
        }
        return amount;
    }

    private void drawCard(Player player, int key){
        player.onHand.put(key, cardSet.get(0));
        cardSet.remove(0);
    }

    // - - - - - - - -
    public void addPlayer(){
        if(players.size() < maxPlayerNum){
            String name = "";
            int amount, spotNum ;
            System.out.print("Enter a player's name : ");
            name = scanner.nextLine().trim();
            if(name.length() > 0){
                if((amount=getAmount("Enter amount to get chips : $ ")) > 0){
                    if((spotNum=getSpot()) > 0){
                        Player player = new Player(name, amount);
                        players.put(spotNum,player);
                        System.out.println("\nGood luck. Enjoy your time.");
                    }
                }
            } else {                        /*  did not enter any name  */
                System.out.println("Player does not want to join.");
            }
        } else {
            System.out.println("Sorry. No room for a new player.\n");
        }
    }

    private int getAmount(String message){
        String stringAmount = "";
        int amount = 0;
        while (true) {
            System.out.print(message);
            stringAmount = scanner.nextLine().trim();
            amount = convertToNum(stringAmount);
            if(amount < 0) {
                System.out.println("Amount should be a positive number.");
            } else {
                if(amount == 0) {
                    System.out.println("Player does not want this time.");
                }
                return amount;
            }
        }
    }

    private int getSpot() {
        String availableSpot = "", stringSpotNum = "";
        for (int i = 1; i <= maxPlayerNum; i++) {
            if (!players.containsKey(i)) {
                availableSpot = availableSpot.concat(i + ", ");
            }
        }
        System.out.print("Available spot(s) : " + availableSpot + "\n");
        while (true) {
            System.out.print("Enter a spot# to play ... ");
            stringSpotNum = scanner.nextLine().trim();
            if (stringSpotNum.length() == 0) {
                System.out.println("Player does not want to join.");
                return 0;
            }
            int x = convertToNum(stringSpotNum);
            if (x > 0 && x <= maxPlayerNum) {
                if (!players.containsKey(x)) {
                    return x;
                } else {
                    System.out.println("Sorry. The spot is occupied.");
                }
            } else {
                System.out.println("The number is out of range.");
            }
        }
    }

    private int convertToNum(String stringNum){
        try {
            return Integer.parseInt(stringNum);
        } catch (NumberFormatException e){
            System.out.println("Invalid entry. Try again.");
            return 0;
        }
    }

    // - - - - - - - -
    public void removePlayer() {
        int spotNum = pickPlayer("\nEnter a spot # to pick a player out ... ");
        if (spotNum > 0 && spotNum <= maxPlayerNum) {
            players.remove(spotNum);
            System.out.println("Bye now. Enjoy your day.");
        }
    }

    public void getMoreChips() {
        int spotNum = pickPlayer("\nEnter a spot # to get more chips ... ");
        if (spotNum > 0 && spotNum <= maxPlayerNum) {
            buyChips(players.get(spotNum), "Enter amount to get chips : $ ");
        }
    }

    public void showScore() {
        int spotNum = pickPlayer("\nEnter a spot # to see the score ... ");
        if (spotNum > 0 && spotNum <= maxPlayerNum) {
            System.out.println(players.get(spotNum).getScore());
        }
    }

    private int pickPlayer(String message) {
        String stringSpotNum = "";
        int spotNum = 0;
        if (showPlayersList()) {
            System.out.print(message);
            stringSpotNum = scanner.nextLine().trim();
            spotNum = convertToNum(stringSpotNum);
            if (players.containsKey(spotNum)) {
                return spotNum;
            } else {
                System.out.println("Wrong entry.");
                return 0;
            }
        } else {
            return 0;
        }
    }

    private boolean showPlayersList(){
        if(players.size() > 0) {
            System.out.println("\n *** Player's List ***");
            for (Integer i : players.keySet()) {
                System.out.println("Spot # " + i + ",  " + players.get(i).getName() + "($ " +
                        players.get(i).getCurrentAmount() + ")");
            }
            return true;
        } else {
            System.out.println("No player. Add a play first.");
            return false;
        }
    }

    public void shuffleCards(int numOfDeck){
        cardSet.clear();
        for(int i=0; i<numOfDeck*4; i++){
            for(int j=1;j<14;j++){
                switch (j) {
                    case 1:
                        cardSet.add("A");
                        break;
                    case 11:
                        cardSet.add("10");
                        break;
                    case 12:
                        cardSet.add("10");
                        break;
                    case 13:
                        cardSet.add("10");
                        break;
                    default:
                        cardSet.add(String.format("%1d",j));
                }
            }
        }
        Collections.shuffle(cardSet);
        Collections.shuffle(cardSet);
        Collections.shuffle(cardSet);
        Collections.shuffle(cardSet);
        showCards(numOfDeck*52);
    }
}
