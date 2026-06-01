package mytabungan.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mytabungan.models.Achievement;
import mytabungan.models.ChartData;
import mytabungan.models.Deposit;
import mytabungan.models.UserStats;

public class GrowthDAO {
    public UserStats getUserStats(int userId) {
        double totalDeposit = getTotalDeposit(userId);
        int depositCount = getDepositCount(userId);
        double avgDeposit = getAverageDeposit(userId);
        int streak = getStreak(userId);
        int wishlistCount = getWishlistCount(userId);
        int reachedWishlist = getReachedWishlistCount(userId);
        int points = calculatePoints( depositCount, streak, wishlistCount, reachedWishlist );
        int level = getLevel(points);
        String levelName =getLevelName(points);
        int nextLevelPoints = getNextLevelTarget(level);

        return new UserStats(points,level,levelName,nextLevelPoints,streak,totalDeposit,
        depositCount,avgDeposit,getAchievements(userId),getMotivationMessage(streak, level));
    }

    public List<Achievement> getAchievements(int userId) {
        List<Achievement> achievements = new ArrayList<>();

        int depositCount = getDepositCount(userId);
        int streak = getStreak(userId);
        int wishlistCount = getWishlistCount(userId);
        int reachedWishlist = getReachedWishlistCount(userId);
        double totalDeposit = getTotalDeposit(userId);

        int points = calculatePoints( depositCount, streak, wishlistCount, reachedWishlist);
        int level = getLevel(points);

        achievements.add(
            new Achievement("Deposit Pertama","🌱",depositCount >= 1)
        );
        achievements.add(
            new Achievement("Penabung Aktif","💰",depositCount >= 10)
        );
        achievements.add(
            new Achievement("Streak 3 Hari","🔥",streak >= 3)
        );
        achievements.add(
            new Achievement("Streak 7 Hari","🔥",streak >= 7)
        );
        achievements.add(
            new Achievement("Wishlist Pertama","🎯",wishlistCount >= 1)
        );
        achievements.add(
            new Achievement("Wishlist Tercapai","🏆",reachedWishlist >= 1)
        );
        achievements.add(
            new Achievement("Total Tabungan 1 Juta","💎",totalDeposit >= 1_000_000)
        );
        achievements.add(
            new Achievement("Level Maksimal","👑",level >= 5)
        );

        return achievements;
    }

    private int calculatePoints(int depositCount,int streak,int wishlistCount,int reachedWishlist) {
        int points = 0;
        points += depositCount * 10;
        points += streak * 20;
        points += wishlistCount * 30;
        points += reachedWishlist * 100;
        return points;
    }

    private String getLevelName(int points) {
        if (points >= 3500)
            return "Sultan Desa";
        if (points >= 2000)
            return "Disiplin";
        if (points >= 1000)
            return "Konsisten";
        if (points >= 500)
            return "Penabung";
        return "Pemula";
    }

    private int getNextLevelTarget(int level) {
        switch (level) {
            case 1: return 500;
            case 2: return 1000;
            case 3: return 2000;
            case 4: return 3500;
            default: return 5000;
        }
    }

    private int getLevel(int points) {
        if (points >= 3500)
            return 5;
        if (points >= 2000)
            return 4;
        if (points >= 1000)
            return 3;
        if (points >= 500)
            return 2;
        return 1;
    }

    private int getStreak(int userId) {
        DepositDAO depositDAO = new DepositDAO();
        List<Deposit> deposits = depositDAO.getDepositsByUserId(userId);
        if (deposits.isEmpty()) {
            return 0;
        }

        Set<LocalDate> activeDays = new HashSet<>();
        for (Deposit d : deposits) {
            activeDays.add(
                d.getCreatedAt().toLocalDate()
            );
        }
        LocalDate currentDay = LocalDate.now();
        int streak = 0;

        while (activeDays.contains(currentDay)) {
            streak++;
            currentDay = currentDay.minusDays(1);
        }
        return streak;
    }

    private String getMotivationMessage(int streak, int level) {
        if (streak >= 7) {
            return "Luar biasa! Kamu konsisten menabung setiap hari.";
        }
        if (level >= 4) {
            return "Sedikit lagi menuju Sultan Desa!";
        }
        if (streak >= 3) {
            return "Pertahankan streak-mu!";
        }
        return "Mulai dari kecil, konsisten adalah kunci.";
    }

    // Method Helper
    private int getDepositCount(int userId) {
        DepositDAO dao = new DepositDAO();
        return dao.getDepositsByUserId(userId).size();
    }
    private double getTotalDeposit(int userId) {
        DepositDAO dao = new DepositDAO();
        return dao.getTotalDepositByUser(userId);
    }
    private double getAverageDeposit(int userId) {
        int count = getDepositCount(userId);
        if (count == 0)
            return 0;
        return getTotalDeposit(userId) / count;
    }
    private int getWishlistCount(int userId) {
        WishlistDAO dao = new WishlistDAO();
        return dao.getWishlistsByUserId(userId).size();
    }
    private int getReachedWishlistCount(int userId) {
        WishlistDAO dao = new WishlistDAO();
        return dao.getReachedWishlistsByUserId(userId).size();
    }

    public List<ChartData> getMonthlyChart(int userId) {
        DepositDAO depositDAO = new DepositDAO();
        List<Deposit> deposits = depositDAO.getDepositsByUserId(userId);

        double[] monthlyTotals = new double[12];
        for (Deposit d : deposits) {
            int month = d.getCreatedAt().getMonthValue();
            monthlyTotals[month - 1] += d.getAmount();
        }

        String[] labels = {
            "Jan","Feb","Mar","Apr","Mei","Jun",
            "Jul","Agu","Sep","Okt","Nov","Des"
        };
        List<ChartData> chart = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            chart.add(
                new ChartData(
                    labels[i],
                    monthlyTotals[i]
                )
            );
        }
        return chart;
    }
}