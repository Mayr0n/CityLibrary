package xyz.nyroma.towny.citymanagement;

import xyz.nyroma.towny.enums.TaxesState;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class CityManager {
    private static boolean logger = true;

    public static void log(String txt) {
        if (logger) {
            System.out.println(txt);
        }
    }

    public TaxesState applyTaxes(City city) {
        float taxes = city.getMoneyManager().getTaxes();
        if (city.getMoneyManager().removeMoney(taxes)) {
            city.setFaillite(false);
            log(city.getName() + " a été débité de " + taxes + " Nyr.");
            return TaxesState.PAYED;
        } else {
            if (city.getFaillite()) {
                removeCity(city);
                log(city.getName() + " a été supprimée.");
                return TaxesState.REMOVED;
            } else {
                city.setFaillite(true);
                log(city.getName() + " est passée en faillite.");
                return TaxesState.BROKEN;
            }
        }
    }

    public boolean isAlreadyOwner(String name) {
        for (City city : CitiesCache.getCities()) {
            if (city.getOwner().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean removeCity(City city) {
        File cityFile = new File("data/towny/" + "cities/" + city.getID() + ".json");
        cityFile.delete();
        return CitiesCache.remove(city);
    }

    public boolean isMemberOfACity(String pseudo) {
        for (City city : CitiesCache.getCities()) {
            for (String member : city.getMembersManager().getMembers()) {
                if (member.equals(pseudo)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Optional<City> getCityOfMember(String pseudo) {
        for (City city : CitiesCache.getCities()) {
            if (city.getMembersManager().isMember(pseudo)) {
                return Optional.of(city);
            }
        }
        return Optional.empty();
    }


    public boolean isAOwner(String pseudo) {
        return getOwnersCity(pseudo).isPresent();
    }

    public Optional<City> getOwnersCity(String pseudo) {
        List<City> cities = CitiesCache.getCities();
        for (City city : cities) {
            if (city.getOwner().equals(pseudo)) {
                return Optional.of(city);
            }
        }
        return Optional.empty();
    }

    public Optional<City> getClaimer(String world, int X, int Z) {
        for (City city : CitiesCache.getCities()) {
            if (city.getClaimsManager().contains(world, X, Z)) {
                return Optional.of(city);
            }
        }
        return Optional.empty();
    }
}
