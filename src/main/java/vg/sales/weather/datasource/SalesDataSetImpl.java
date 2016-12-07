package vg.sales.weather.datasource;

import java.sql.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import vg.sales.weather.model.Sales;

/**
 *
 * @author vagner
 */
public class SalesDataSetImpl implements SalesDataSet {

    private EntityManager MANAGER;

    @Override
    public List<Sales> listSales(Date initialDate, Date finalDate)  {
        MANAGER = Persistence.createEntityManagerFactory("sales-weather").createEntityManager();
        MANAGER.getTransaction().begin();
         String jpql = ("SELECT \n" +
"   f.idfilial,\n" +
"   f.numerofilial,\n" +
"   f.fantasia,\n" +
"   COALESCE(v_pro.totalitem,0) AS pro_totalitem,\n" +
"   COALESCE(d_pro.totalitem,0) AS pro_devolucao,\n" +
"   COALESCE(v_pro.totalitem,0) - COALESCE(d_pro.totalitem,0) AS p_liquido,\n" +
"   COALESCE(v_sg.totalitem,0) AS sg_totalitem,      \n" +
"   COALESCE(d_sg.totalitem,0) AS sg_devolucao,\n" +
"   COALESCE(v_sg.totalitem,0) - COALESCE(d_sg.totalitem,0) AS sg_liquido,\n" +
"   COALESCE(v_rc.totalitem,0) - COALESCE(d_rc.totalitem,0) AS rc_liquido,\n" +
"   (COALESCE(v_pro.totalitem,0) - COALESCE(d_pro.totalitem,0)) + (COALESCE(v_sg.totalitem,0) - COALESCE(d_sg.totalitem,0)) + (COALESCE(v_rc.totalitem,0) - COALESCE(d_rc.totalitem,0)) + (COALESCE(v_pre.totalitem,0) - COALESCE(d_pre.totalitem,0)) + (COALESCE(v_ener.totalitem,0) - COALESCE(d_ener.totalitem,0)) AS total_liquido\n" +
"  FROM glb.filial f LEFT JOIN \n" +
"\n" +
"                              (SELECT \n" +
"                                ib.idfilial,\n" +
"                                ROUND(SUM(pmi.quantidade * (ib.totalpresente / ib.quantidade)), 2) AS totalpresente,\n" +
"                                ROUND(SUM(pmi.quantidade * (ib.totalitem / ib.quantidade)), 2) AS totalitem   \n" +
"\n" +
"                               FROM rst.pedidovendamovimentoitem pmi INNER JOIN rst.itembase ib ON pmi.idfilial = ib.idfilial AND\n" +
"                                                                                                   pmi.iditembase = ib.iditembase\n" +
"                                                                     LEFT JOIN glb.produtograde pg ON pg.idproduto = ib.idproduto AND\n" +
"                                                                                                      pg.idgradex = ib.idgradex AND\n" +
"                                                                                                      pg.idgradey = ib.idgradey\n" +
"                               WHERE pmi.idfilial IN (10001,10003,10004,10005,10007,10008,10009,10010,10011,10012,10013,10014,10015,10016,10017,10018,10019,10020,10021,10024) AND\n" +
"                                     pmi.datamovimento BETWEEN :initialdate AND :initialdate AND                                     \n" +
"                                     pmi.idsituacaopedidovenda = 3 AND\n" +
"                                     pg.idtipoproduto <> 9 \n" +
"\n" +
"                               GROUP BY  ib.idfilial\n" +
"                               ORDER BY ib.idfilial) AS v_pro   ON v_pro.idfilial = f.idfilial\n" +
"                               \n" +
"                               \n" +
"                    LEFT JOIN\n" +
"                    \n" +
"                              (SELECT \n" +
"                                ib.idfilial,\n" +
"                                ROUND(SUM(pmi.quantidade * (ib.totalpresente / ib.quantidade)), 2) AS totalpresente,\n" +
"                                ROUND(SUM(pmi.quantidade * (ib.totalitem / ib.quantidade)), 2) AS totalitem   \n" +
"\n" +
"                               FROM rst.pedidovendamovimentoitem pmi INNER JOIN rst.itembase ib ON pmi.idfilial = ib.idfilial AND\n" +
"                                                                                                   pmi.iditembase = ib.iditembase\n" +
"                                                                     LEFT JOIN glb.produtograde pg ON pg.idproduto = ib.idproduto AND\n" +
"                                                                                                      pg.idgradex = ib.idgradex AND\n" +
"                                                                                                      pg.idgradey = ib.idgradey\n" +
"                               WHERE pmi.idfilial IN (10001,10003,10004,10005,10007,10008,10009,10010,10011,10012,10013,10014,10015,10016,10017,10018,10019,10020,10021,10024) AND\n" +
"                                     pmi.datamovimento BETWEEN :initialdate AND :initialdate AND                                     \n" +
"                                     pmi.idsituacaopedidovenda = 5 AND\n" +
"                                     pg.idtipoproduto <> 9 \n" +
"\n" +
"                               GROUP BY  ib.idfilial\n" +
"                               ORDER BY ib.idfilial) AS d_pro   ON d_pro.idfilial = f.idfilial           \n" +
"                               \n" +
"                    LEFT JOIN \n" +
"                    \n" +
"                              (SELECT \n" +
"                                 its.idfilial,\n" +
"                                 ROUND(SUM(its.totalpresente), 2) AS totalpresente,\n" +
"                                 ROUND(SUM(its.totalitem), 2) AS totalitem\n" +
"              \n" +
"                                 FROM  rst.itemservico its INNER JOIN glb.filial f ON its.idfilial   = f.idfilial\n" +
"                                                           INNER JOIN glb.servico se ON se.idservico = its.idservico\n" +
"                                                           INNER JOIN glb.gruposervico gs ON gs.idgruposervico = se.idgruposervico\n" +
"                                                    \n" +
"                               WHERE its.idfilial IN (10001,10003,10004,10005,10007,10008,10009,10010,10011,10012,10013,10014,10015,10016,10017,10018,10019,10020,10021,10024) AND\n" +
"                                     its.datamovimento BETWEEN :initialdate AND :initialdate AND\n" +
"                                     gs.idtiposervico = 2 AND \n" +
"                                     its.idoperacaoservico = 202010 \n" +
"                               GROUP BY its.idfilial) AS v_sg   ON v_sg.idfilial = f.idfilial                      \n" +
"                               \n" +
"                    LEFT JOIN\n" +
"                    \n" +
"                                                            (SELECT \n" +
"                                 its.idfilial,\n" +
"                                 ROUND(SUM(its.totalpresente), 2) AS totalpresente,\n" +
"                                 ROUND(SUM(its.totalitem), 2) AS totalitem\n" +
"              \n" +
"                                 FROM  rst.itemservico its INNER JOIN glb.filial f ON its.idfilial   = f.idfilial\n" +
"                                                           INNER JOIN glb.servico se ON se.idservico = its.idservico\n" +
"                                                           INNER JOIN glb.gruposervico gs ON gs.idgruposervico = se.idgruposervico\n" +
"                                                    \n" +
"                               WHERE its.idfilial IN (10001,10003,10004,10005,10007,10008,10009,10010,10011,10012,10013,10014,10015,10016,10017,10018,10019,10020,10021,10024) AND\n" +
"                                     its.datamovimento BETWEEN :initialdate AND :initialdate AND\n" +
"                                     gs.idtiposervico = 2 AND \n" +
"                                     its.idoperacaoservico = 201020 \n" +
"                               GROUP BY its.idfilial) AS d_sg   ON d_sg.idfilial = f.idfilial                                  \n" +
"                               \n" +
"                               \n" +
"                    LEFT JOIN \n" +
"                    \n" +
"                              (SELECT \n" +
"                                 its.idfilial,\n" +
"                                 ROUND(SUM(its.totalpresente), 2) AS totalpresente,\n" +
"                                 ROUND(SUM(its.totalitem), 2) AS totalitem\n" +
"              \n" +
"                                 FROM  rst.itemservico its INNER JOIN glb.filial f ON its.idfilial   = f.idfilial\n" +
"                                                           INNER JOIN glb.servico se ON se.idservico = its.idservico\n" +
"                                                           INNER JOIN glb.gruposervico gs ON gs.idgruposervico = se.idgruposervico\n" +
"                                                    \n" +
"                               WHERE its.idfilial IN (10001,10003,10004,10005,10007,10008,10009,10010,10011,10012,10013,10014,10015,10016,10017,10018,10019,10020,10021,10024) AND\n" +
"                                     its.datamovimento BETWEEN :initialdate AND :initialdate AND\n" +
"                                     gs.idtiposervico IN (11,12,13) AND\n" +
"                                     its.idoperacaoservico = 202010\n" +
"                               GROUP BY its.idfilial) AS v_rc   ON v_rc.idfilial = f.idfilial                      \n" +
"                               \n" +
"                    LEFT JOIN\n" +
"                    \n" +
"                                                            (SELECT \n" +
"                                 its.idfilial,\n" +
"                                 ROUND(SUM(its.totalpresente), 2) AS totalpresente,\n" +
"                                 ROUND(SUM(its.totalitem), 2) AS totalitem\n" +
"              \n" +
"                                 FROM  rst.itemservico its INNER JOIN glb.filial f ON its.idfilial   = f.idfilial\n" +
"                                                           INNER JOIN glb.servico se ON se.idservico = its.idservico\n" +
"                                                           INNER JOIN glb.gruposervico gs ON gs.idgruposervico = se.idgruposervico\n" +
"                                                    \n" +
"                               WHERE its.idfilial IN (10001,10003,10004,10005,10007,10008,10009,10010,10011,10012,10013,10014,10015,10016,10017,10018,10019,10020,10021,10024) AND\n" +
"                                     its.datamovimento BETWEEN :initialdate AND :initialdate AND\n" +
"                                     gs.idtiposervico IN (11,12,13) AND \n" +
"                                     its.idoperacaoservico = 201020 \n" +
"                               GROUP BY its.idfilial) AS d_rc   ON d_rc.idfilial = f.idfilial\n" +
"                               \n" +
"                               \n" +
"                    LEFT JOIN \n" +
"                    \n" +
"                              (SELECT \n" +
"                                 its.idfilial,\n" +
"                                 ROUND(SUM(its.totalpresente), 2) AS totalpresente,\n" +
"                                 ROUND(SUM(its.totalitem), 2) AS totalitem\n" +
"              \n" +
"                                 FROM  rst.itemservico its INNER JOIN glb.filial f ON its.idfilial   = f.idfilial\n" +
"                                                           INNER JOIN glb.servico se ON se.idservico = its.idservico\n" +
"                                                           INNER JOIN glb.gruposervico gs ON gs.idgruposervico = se.idgruposervico\n" +
"                                                    \n" +
"                               WHERE its.idfilial IN (10001,10003,10004,10005,10007,10008,10009,10010,10011,10012,10013,10014,10015,10016,10017,10018,10019,10020,10021,10024) AND\n" +
"                                     its.datamovimento BETWEEN :initialdate AND :initialdate AND\n" +
"                                     gs.idtiposervico = 3 AND \n" +
"                                     its.idoperacaoservico = 202010 \n" +
"                               GROUP BY its.idfilial) AS v_pre   ON v_pre.idfilial = f.idfilial                      \n" +
"                               \n" +
"                    LEFT JOIN\n" +
"                    \n" +
"                                                            (SELECT \n" +
"                                 its.idfilial,\n" +
"                                 ROUND(SUM(its.totalpresente), 2) AS totalpresente,\n" +
"                                 ROUND(SUM(its.totalitem), 2) AS totalitem\n" +
"              \n" +
"                                 FROM  rst.itemservico its INNER JOIN glb.filial f ON its.idfilial   = f.idfilial\n" +
"                                                           INNER JOIN glb.servico se ON se.idservico = its.idservico\n" +
"                                                           INNER JOIN glb.gruposervico gs ON gs.idgruposervico = se.idgruposervico\n" +
"                                                    \n" +
"                               WHERE its.idfilial IN (10001,10003,10004,10005,10007,10008,10009,10010,10011,10012,10013,10014,10015,10016,10017,10018,10019,10020,10021,10024) AND\n" +
"                                     its.datamovimento BETWEEN :initialdate AND :initialdate AND\n" +
"                                     gs.idtiposervico = 3 AND \n" +
"                                     its.idoperacaoservico = 201020 \n" +
"                               GROUP BY its.idfilial) AS d_pre   ON d_pre.idfilial = f.idfilial\n" +
"                               \n" +
"                               \n" +
"                    LEFT JOIN \n" +
"                    \n" +
"                              (SELECT \n" +
"                                 its.idfilial,\n" +
"                                 ROUND(SUM(its.totalpresente), 2) AS totalpresente,\n" +
"                                 ROUND(SUM(its.totalitem), 2) AS totalitem\n" +
"              \n" +
"                                 FROM  rst.itemservico its INNER JOIN glb.filial f ON its.idfilial   = f.idfilial\n" +
"                                                           INNER JOIN glb.servico se ON se.idservico = its.idservico\n" +
"                                                           INNER JOIN glb.gruposervico gs ON gs.idgruposervico = se.idgruposervico\n" +
"                                                    \n" +
"                               WHERE its.idfilial IN (10001,10003,10004,10005,10007,10008,10009,10010,10011,10012,10013,10014,10015,10016,10017,10018,10019,10020,10021,10024) AND\n" +
"                                     its.datamovimento BETWEEN :initialdate AND :initialdate AND\n" +
"                                     gs.idtiposervico = 16 AND \n" +
"                                     its.idservico = 47 AND \n" +
"                                     its.idoperacaoservico = 202010 \n" +
"                               GROUP BY its.idfilial) AS v_ener   ON v_ener.idfilial = f.idfilial                      \n" +
"                               \n" +
"                    LEFT JOIN\n" +
"                    \n" +
"                                                            (SELECT \n" +
"                                 its.idfilial,\n" +
"                                 ROUND(SUM(its.totalpresente), 2) AS totalpresente,\n" +
"                                 ROUND(SUM(its.totalitem), 2) AS totalitem\n" +
"              \n" +
"                                 FROM  rst.itemservico its INNER JOIN glb.filial f ON its.idfilial   = f.idfilial\n" +
"                                                           INNER JOIN glb.servico se ON se.idservico = its.idservico\n" +
"                                                           INNER JOIN glb.gruposervico gs ON gs.idgruposervico = se.idgruposervico\n" +
"                                                    \n" +
"                               WHERE its.idfilial IN (10001,10003,10004,10005,10007,10008,10009,10010,10011,10012,10013,10014,10015,10016,10017,10018,10019,10020,10021,10024) AND\n" +
"                                     its.datamovimento BETWEEN :initialdate AND :initialdate AND\n" +
"                                     gs.idtiposervico = 16 AND \n" +
"                                     its.idservico = 47 AND \n" +
"                                     its.idoperacaoservico = 201020 \n" +
"                               GROUP BY its.idfilial) AS d_ener   ON d_ener.idfilial = f.idfilial\n" +
"							          WHERE f.idfilial NOT IN (10900, 10901, 10002, 10006, 10023, 10025, 10022)\n" +
"                               ");
         
         List<Sales> sales = MANAGER.createNativeQuery(jpql, Sales.class).setParameter("initialdate", initialDate).getResultList();
         MANAGER.getTransaction().commit();
         MANAGER.close();

            return sales;

    }
}
