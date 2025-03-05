package com.hm.stock.domain.stockdata.reset.vo;

public class DcResponse {
    /**
     * rc : 0
     * rt : 4
     * svr : 182994603
     * lt : 2
     * full : 1
     * dlmkts : 8,10,128
     * data : {"f43":1798803,"f44":1820052,"f45":1795497,"f46":1813110,"f47":10806270976,"f48":4.1820332032E10,"f57":"HSI","f58":"恒生指数","f59":2,"f60":1802589,"f86":1694577600,"f107":100,"f152":2,"f169":-3786,"f170":-21,"f171":136,"f292":3}
     */

    private long rc;
    private long rt;
    private long svr;
    private long lt;
    private long full;
    private String dlmkts;
    private DataBean data;

    public long getRc() {
        return rc;
    }

    public void setRc(long rc) {
        this.rc = rc;
    }

    public long getRt() {
        return rt;
    }

    public void setRt(long rt) {
        this.rt = rt;
    }

    public long getSvr() {
        return svr;
    }

    public void setSvr(long svr) {
        this.svr = svr;
    }

    public long getLt() {
        return lt;
    }

    public void setLt(long lt) {
        this.lt = lt;
    }

    public long getFull() {
        return full;
    }

    public void setFull(long full) {
        this.full = full;
    }

    public String getDlmkts() {
        return dlmkts;
    }

    public void setDlmkts(String dlmkts) {
        this.dlmkts = dlmkts;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * f43 : 1798803
         * f44 : 1820052
         * f45 : 1795497
         * f46 : 1813110
         * f47 : 10806270976
         * f48 : 4.1820332032E10
         * f57 : HSI
         * f58 : 恒生指数
         * f59 : 2
         * f60 : 1802589
         * f86 : 1694577600
         * f107 : 100
         * f152 : 2
         * f169 : -3786
         * f170 : -21
         * f171 : 136
         * f292 : 3
         */

        private long f43;
        private long f44;
        private long f45;
        private long f46;
        private long f47;
        private double f48;
        private String f57;
        private String f58;
        private long f59;
        private long f60;
        private long f86;
        private long f107;
        private long f152;
        private long f169;
        private long f170;
        private long f171;
        private long f292;

        public long getF43() {
            return f43;
        }

        public void setF43(long f43) {
            this.f43 = f43;
        }

        public long getF44() {
            return f44;
        }

        public void setF44(long f44) {
            this.f44 = f44;
        }

        public long getF45() {
            return f45;
        }

        public void setF45(long f45) {
            this.f45 = f45;
        }

        public long getF46() {
            return f46;
        }

        public void setF46(long f46) {
            this.f46 = f46;
        }

        public long getF47() {
            return f47;
        }

        public void setF47(long f47) {
            this.f47 = f47;
        }

        public double getF48() {
            return f48;
        }

        public void setF48(double f48) {
            this.f48 = f48;
        }

        public String getF57() {
            return f57;
        }

        public void setF57(String f57) {
            this.f57 = f57;
        }

        public String getF58() {
            return f58;
        }

        public void setF58(String f58) {
            this.f58 = f58;
        }

        public long getF59() {
            return f59;
        }

        public void setF59(long f59) {
            this.f59 = f59;
        }

        public long getF60() {
            return f60;
        }

        public void setF60(long f60) {
            this.f60 = f60;
        }

        public long getF86() {
            return f86;
        }

        public void setF86(long f86) {
            this.f86 = f86;
        }

        public long getF107() {
            return f107;
        }

        public void setF107(long f107) {
            this.f107 = f107;
        }

        public long getF152() {
            return f152;
        }

        public void setF152(long f152) {
            this.f152 = f152;
        }

        public long getF169() {
            return f169;
        }

        public void setF169(long f169) {
            this.f169 = f169;
        }

        public long getF170() {
            return f170;
        }

        public void setF170(long f170) {
            this.f170 = f170;
        }

        public long getF171() {
            return f171;
        }

        public void setF171(long f171) {
            this.f171 = f171;
        }

        public long getF292() {
            return f292;
        }

        public void setF292(long f292) {
            this.f292 = f292;
        }
    }
}
