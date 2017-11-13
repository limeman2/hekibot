package main.java.diet.nutella.hekibot.controller;

import com.google.common.collect.ImmutableMap;
import main.java.diet.nutella.hekibot.model.UserDAO;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.UserNoticeEvent;

public class SubscriberListener extends ListenerAdapter {
    private UserDAO dao;


    public SubscriberListener(UserDAO dao) {
        this.dao = dao;
    }

    @Override
    public void onUserNotice(UserNoticeEvent event) throws Exception {
        ImmutableMap<String, String> tags = event.getTags();

        if (tags.get("msg-id").equals("raid")) return;

        StringBuilder messageOne = new StringBuilder();
        StringBuilder messageTwo = new StringBuilder();
        String subPlan = tags.get("msg-param-sub-plan");

        messageOne.append("Thank you for ");
        if (tags.get("msg-id").equals("resub")) messageOne.append("re");
        messageOne.append("subscribing");

        if (subPlan.equals("2000")) {
            messageOne.append(" with tier 2, ");
        } else if (subPlan.equals("3000")) {
            messageOne.append(" with tier 3, ");
        } else {
            messageOne.append(", ");

        }
        messageOne.append(tags.get("login"))
                .append("! You have just received an extra ");

        int amountOfBonus = 500;


        if (subPlan.equals("2000")) {
            amountOfBonus = 750;
        } else if (subPlan.equals("3000")) {
            amountOfBonus = 1000;
        }

        messageOne.append(amountOfBonus)
                .append(" hekicoins PogChamp ");

        messageTwo.append("hekimaW hekimaW hekimaW ");

        event.respondWith(messageOne.toString());
        event.respondWith(messageTwo.toString());

        this.dao.addCoinsToUser(tags.get("login"), amountOfBonus);
    }
}
